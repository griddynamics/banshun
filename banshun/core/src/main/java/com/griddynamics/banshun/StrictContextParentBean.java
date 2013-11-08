/**
 *    Copyright 2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 *    http://www.griddynamics.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *  @Project: Banshun
 * */
package com.griddynamics.banshun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author Alexey Olenev
 */
public class StrictContextParentBean extends ContextParentBean implements BeanNameAware {

    private static final Logger log = LoggerFactory.getLogger(StrictContextParentBean.class);

    private String name;
    private List<String> runOnlyServices = new ArrayList<String>();
    private LocationsGraph locationsGraph;

    private boolean prohibitCycles = true;

    public void setProhibitCycles(boolean prohibitCycles) {
        this.prohibitCycles = prohibitCycles;
    }

    public String getName() {
        return name;
    }

    public void setBeanName(String name) {
        this.name = name;
    }

    public void setRunOnlyServices(String[] runOnlyServices) {
        List<String> runOnly = new ArrayList<String>();
        for (String service : runOnlyServices) {
            runOnly.add(service + EXPORT_REF_SUFFIX);
        }
        this.runOnlyServices = runOnly;
    }

    @Override
    protected void addToFailedLocations(String loc) {
        locationsGraph.transitiveClosure(loc, ignoredLocations, false);
    }

    @Override
    protected List<String> analyzeDependencies(List<String> configLocations) throws Exception {
        ContextAnalyzer analyzer = new ContextAnalyzer();
        List<Exception> exceptions = new LinkedList<Exception>();

        List<String> limitedLocations = new ArrayList<String>();
        for (String loc : configLocations) {
            BeanDefinitionRegistry beanFactory = getBeanFactory(loc);

            String[] beanNames = beanFactory.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                try {
                    if (isExport(beanDefinition)) {
                        analyzer.addExport(beanDefinition, loc);
                        if (checkForRunOnly(beanName)) {
                            limitedLocations.add(loc);
                        }
                    } else if (isImport(beanDefinition)) {
                        analyzer.addImport(beanDefinition, loc);
                    } else if (beanDefinition.getBeanClassName() != null) {
                        checkClassExist(loc, beanName, beanDefinition.getBeanClassName());
                    }
                } catch (Exception ex) {
                    exceptions.add(ex);
                }
            }
        }

        analyzer.areThereExportsWithoutImport();

        if (analyzer.areThereImportsWithoutExports() || !analyzer.areImportsTypesCorrect()) {
            exceptions.add(new BeanDefinitionValidationException(
                    "There are severe errors while parsing contexts. See logs for details"));
        }
        
        if (!exceptions.isEmpty()) {
            for (Exception exception : exceptions) {
                log.error(exception.getMessage());
            }
            throw exceptions.get(0);
        }

        DependencySorter sorter = new DependencySorter(configLocations.toArray(new String[0]), analyzer.getImports(), analyzer.getExports());
        sorter.setProhibitCycles(prohibitCycles);

        locationsGraph = new LocationsGraph(analyzer.getImports(), analyzer.getExports());
        List<String> analyzedConfigLocations = locationsGraph.filterConfigLocations(limitedLocations, sorter.sort());

        log.info("ordered list of the contexts: {}", analyzedConfigLocations);

        return analyzedConfigLocations;
    }

    private void checkClassExist(String location, String beanName, String beanClassName) throws ClassNotFoundException {
        try {
            Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException (MessageFormat.format(
                    "Class not found {0} in location: {1} for bean: {2}", beanClassName, location, beanName));
        }
    }

    private BeanDefinitionRegistry getBeanFactory(String location) {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(new SimpleBeanDefinitionRegistry());
        beanDefinitionReader.setResourceLoader(context);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(context));
        beanDefinitionReader.loadBeanDefinitions(location);

        return beanDefinitionReader.getBeanFactory();
    }

    /**
     * Check whether bean will be imported into other contexts.
     *
     * @param beanDefinition Definition of the bean to check.
     * @return <tt>true</tt> if the bean will be imported, <tt>false</tt> otherwise.
     */
    private boolean isImport(BeanDefinition beanDefinition) {
        if (beanDefinition.getFactoryMethodName() != null) {
            if (beanDefinition.getFactoryMethodName().equals("lookup")
                && beanDefinition.getFactoryBeanName().equals(getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether bean will be exported from this context into others.
     *
     * @param beanDefinition Definition of the bean to check.
     * @return <tt>true</tt> if the bean will be exported, <tt>false</tt> otherwise.
     */
    private boolean isExport(BeanDefinition beanDefinition) {
        if (beanDefinition.getFactoryMethodName() != null) {
            if (beanDefinition.getFactoryMethodName().equals("export")
                && beanDefinition.getFactoryBeanName().equals(getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkForRunOnly(String beanName) {
        return !runOnlyServices.isEmpty() && runOnlyServices.contains(beanName);
    }
}
