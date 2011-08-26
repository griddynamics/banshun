package com.macys.platform.util.spring.nested;

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
import java.util.LinkedList;
import java.util.List;

/**
 * Singleton bean that instantiates nested children contexts by the give resources.
 * The error handling is strict during instantiation of nested contexts.
 * @author Alexey Olenev
 *
 */
public class StrictContextParentBean extends ContextParentBean implements BeanNameAware {

    private static final Logger log = LoggerFactory.getLogger(StrictContextParentBean.class);

    private String name;

    public String getName() {
        return name;
    }

    public void setBeanName(String name) {
        this.name = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        analyzeDependencies();
        super.afterPropertiesSet();
    }

    private void analyzeDependencies() throws Exception {
        ContextAnalyzer analyzer = new ContextAnalyzer();
        List<Exception> exceptions = new LinkedList<Exception>();

        for (String loc : getConfigLocations()) {
            BeanDefinitionRegistry beanFactory = getBeanFactory(loc);

            String[] beanNames = beanFactory.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                try {
                    if (isExport(beanDefinition)) {
                        analyzer.addExport(beanDefinition, loc);
                    } else if (isImport(beanDefinition)) {
                        analyzer.addImport(beanDefinition, loc);
                    } else if (beanDefinition.getBeanClassName() != null) {
                        checkClass(loc, beanName, beanDefinition.getBeanClassName());
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
        setConfigLocations(sorter.sort());
    }

    private void checkClass(String location, String beanName, String beanClassName) throws ClassNotFoundException {
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
}