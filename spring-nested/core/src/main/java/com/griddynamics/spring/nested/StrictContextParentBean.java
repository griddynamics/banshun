package com.griddynamics.spring.nested;

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
 * Copyright (c) 2011 Grid Dynamics Consulting Services, Inc, All Rights
 * Reserved http://www.griddynamics.com
 * 
 * For information about the licensing and copyright of this document please
 * contact Grid Dynamics at info@griddynamics.com.
 * 
 * $Id: $
 * 
 * @Project: Spring Nested
 * @Description: Singleton bean that instantiates nested children contexts by the give resources.
 * The error handling is strict during instantiation of nested contexts.
 * @author Alexey Olenev
 *
 */
public class StrictContextParentBean extends ContextParentBean implements BeanNameAware {

    private static final Logger log = LoggerFactory.getLogger(StrictContextParentBean.class);

    private String name;
    private List<String> runOnlyServices = new ArrayList<String>();
    private Map<String, HashSet<String>> importsByExport;

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
    protected void resolveConfigLocations() throws Exception {
    }

    @Override
    protected void addToFailedLocations(String loc) {
        transitiveClosure(loc, ignoredLocations, importsByExport);
    }

    @Override
    protected void analyzeDependencies() throws Exception {
        ContextAnalyzer analyzer = new ContextAnalyzer();
        List<Exception> exceptions = new LinkedList<Exception>();

        List<String> limitedLocations = new ArrayList<String>();
        for (String loc : getConfigLocations()) {
            BeanDefinitionRegistry beanFactory = getBeanFactory(loc);

            String[] beanNames = beanFactory.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                try {
                    if (isExport(beanDefinition)) {
                        analyzer.addExport(beanDefinition, loc);
                        if (!runOnlyServices.isEmpty() && checkForRunOnly(beanName)) {
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
            exceptions.add(new BeanDefinitionValidationException("There are severe errors while parsing contexts. See logs for details"));
        }
        
        if (!exceptions.isEmpty()) {
            for (Exception exception : exceptions) {
                log.error(exception.getMessage());
            }
            throw exceptions.get(0);
        }

        DependencySorter sorter = new DependencySorter(getConfigLocations(), analyzer.getImports(), analyzer.getExports());
        sorter.setProhibitCycles(prohibitCycles);

        filterConfigLocations(limitedLocations, sorter.sort(), analyzer.getImports(), analyzer.getExports());

        log.info("Contexts were created in that order: " + Arrays.toString(getConfigLocations()));
    }

    private void checkClassExist(String location, String beanName, String beanClassName) throws ClassNotFoundException {
        try {
            Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException (MessageFormat.format("Class not found {0} in location: {1} for bean: {2}", beanClassName, location, beanName));
        }
    }

    private BeanDefinitionRegistry getBeanFactory(String location) {
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(new SimpleBeanDefinitionRegistry());
        beanDefinitionReader.setResourceLoader(context);
        beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(context));
        beanDefinitionReader.loadBeanDefinitions(location);

        BeanDefinitionRegistry beanFactory = beanDefinitionReader.getBeanFactory();

        return beanFactory;
    }

    /**
     * Check whether bean will be imported into other contexts.
     * @param beanDefinition
     * @return
     */
    private boolean isImport(BeanDefinition beanDefinition) {
        if(beanDefinition.getFactoryMethodName() != null) {
            if (beanDefinition.getFactoryMethodName().equals("lookup")
                && beanDefinition.getFactoryBeanName().equals(getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether bean will be exported from this contexts into the other.
     * @param beanDefinition
     * @return
     */
    private boolean isExport(BeanDefinition beanDefinition) {
        if(beanDefinition.getFactoryMethodName() != null) {
            if (beanDefinition.getFactoryMethodName().equals("export")
                && beanDefinition.getFactoryBeanName().equals(getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkForRunOnly(String beanName) {
        return runOnlyServices.contains(beanName);
    }

    private void filterConfigLocations(List<String> limitedLocations, String[] allLocations,
            Map<String, List<BeanReferenceInfo>> imports, Map<String, BeanReferenceInfo> exports) {
        Map<String, HashSet<String>> exportsByImport = new HashMap<String, HashSet<String>>();
        importsByExport = new HashMap<String, HashSet<String>>();

        for (String beanName : imports.keySet()) {
            String expLoc = exports.get(beanName).getLocation();
            if (!importsByExport.containsKey(expLoc)) {
                importsByExport.put(expLoc, new HashSet<String>());
            }
            for (BeanReferenceInfo refInfo : imports.get(beanName)) {
                if (!exportsByImport.containsKey(refInfo.getLocation())) {
                    exportsByImport.put(refInfo.getLocation(), new HashSet<String>());
                }
                exportsByImport.get(refInfo.getLocation()).add(expLoc);
                importsByExport.get(expLoc).add(refInfo.getLocation());
            }
        }

        Set<String> marked = new HashSet<String>();
        if (!limitedLocations.isEmpty()) {
            for (String loc : limitedLocations) {
                transitiveClosure(loc, marked, exportsByImport);
            }

            List<String> resultLocationList = new ArrayList<String>(Arrays.asList(allLocations));
            resultLocationList.retainAll(marked);

            this.configLocations = resultLocationList.toArray(new String[0]);
        }
    }

    private void transitiveClosure(String loc, Set<String> marked, Map<String, HashSet<String>> locationDependencies) {
        marked.add(loc);
        if (locationDependencies.containsKey(loc)) {
            for (String dependsOn : locationDependencies.get(loc)) {
                if (!marked.contains(dependsOn)) {
                    transitiveClosure(dependsOn, marked, locationDependencies);
                }
            }
        }
    }
}