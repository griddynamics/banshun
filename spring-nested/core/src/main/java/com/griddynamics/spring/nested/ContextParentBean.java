package com.griddynamics.spring.nested;

import java.io.IOException;
import java.util.*;

import org.springframework.aop.TargetSource;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * Copyright (c) 2011 Grid Dynamics Consulting Services, Inc, All Rights
 * Reserved http://www.griddynamics.com
 * <p/>
 * For information about the licensing and copyright of this document please
 * contact Grid Dynamics at info@griddynamics.com.
 * <p/>
 * $Id: $
 *
 * @Project: Spring Nested
 * @Description: singleton bean, should be used accordingly with the interface {@link Registry} recommendations.
 * Implementation of the {@link Registry} delegated to simple internal bean {@link RegistryBean}
 * Also, it instantiate the nested children contexts by the given resources. These contexts
 * receives factory bean instantiates this bean as a "parent bean". This bean will be available via
 * this Spring intrinsic feature, by the some well known name.
 * Note: children contexts extends {@link XmlWebApplicationContext}. so it's a little bit straightforward and
 * intended for the current usage
 */
public class ContextParentBean implements InitializingBean, ApplicationContextAware, Registry, DisposableBean
        , ApplicationListener {
    private static final Logger log = LoggerFactory.getLogger(ContextParentBean.class);
    private Map<String, Exception> nestedContextsExceptions = new LinkedHashMap<String, Exception>();

    protected ApplicationContext context;
    private List<ConfigurableApplicationContext> children = new ArrayList<ConfigurableApplicationContext>();

    private String[] configLocations;
    private Registry registry;

    private boolean strictErrorHandling = false;
    private String childContextPrototype = null;

    private String[] fireOnly = null;
    public static final String TARGET_SOURCE_SUFFIX = "_targetSource";
    public static final String BEAN_DEF_SUFFIX = "_beanDef";

    /**
     * specifies whether initialization of this bean failed if one of the nested children contexts
     * failed to build.
     *
     * @default false
     */
    public void setStrictErrorHandling(boolean strictErrorHandling) {
        this.strictErrorHandling = strictErrorHandling;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setChildContextPrototype(String childContextPrototype) {
        this.childContextPrototype = childContextPrototype;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setFireOnly(String[] fireOnly) {
        this.fireOnly = fireOnly;
    }

    /**
     * resolves configs paths and build nested children contexts
     */
    public void afterPropertiesSet() throws Exception {
        for (String loc : configLocations) {
            // attempt to resolve classpath*:
            try {
                Resource[] resources = context.getResources(loc);

                for (final Resource res : resources) {
                    try {
                        ConfigurableApplicationContext child = createChildContext(res, context);
                        children.add(child);
                    } catch (Exception e) {
                        log.error(String.format("Failed to process resource [%s] from location [%s] ", res.getURL(), loc), e);
                        if (strictErrorHandling) {
                            throw e;
                        }
                        nestedContextsExceptions.put(loc, e);
                    }
                }
            } catch (IOException e) {
                log.error(String.format("Failed to process configuration from [%s]", loc), e);
                if (strictErrorHandling) {
                    throw e;
                }
            }
        }
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            if (context.equals(((ContextRefreshedEvent) event).getApplicationContext())) {
                ConfigurableBeanFactory factory = ((AbstractApplicationContext) context).getBeanFactory();
                String[] singletonNames = factory.getSingletonNames();
                for (String singletonName : singletonNames) {
                    if (singletonName.contains(TARGET_SOURCE_SUFFIX)) {
                        String name = singletonName.split(ContextParentBean.TARGET_SOURCE_SUFFIX)[0]
                                + ContextParentBean.BEAN_DEF_SUFFIX;
                        if (!context.containsBean(name)) {
                            ((BeanDefinitionRegistry) factory).registerBeanDefinition(name,
                                    BeanDefinitionBuilder.genericBeanDefinition(factory.getBean(singletonName, TargetSource.class).getTargetClass()).getBeanDefinition());
                        }
                    }
                }
            }
        }
    }

    private ConfigurableApplicationContext createChildContext(Resource res, ApplicationContext parent) throws Exception {
        if (childContextPrototype != null && childContextPrototype.length() > 0) {
            try {
                return (ConfigurableApplicationContext) parent.getBean(childContextPrototype, res, parent);
            } catch (Exception e) {
                log.warn("Can not initialize ApplicationContext " + childContextPrototype + " with configuration location " + res.getURL(), e);
            }
        }

        return new SingleResourceXmlChildContext(res, parent);
    }

    public Map<String, Exception> getNestedContextsExceptions() {
        return nestedContextsExceptions;
    }

    public void setApplicationContext(ApplicationContext arg0)
            throws BeansException {
        context = arg0;
    }

    /**
     * delimiter separated list of Sspring-usual resources specifies. classpath*: classpath:, file:
     * start wildcards are supported.
     * delimiters are {@link ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS}
     */
    public void setConfigLocation(String list) {
        String[] locations = StringUtils.tokenizeToStringArray(list,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);

        Assert.noNullElements(locations, "Config locations must not be null");
        configLocations = new String[locations.length];
        for (int i = 0; i < locations.length; i++) {
            this.configLocations[i] = (SystemPropertyUtils
                    .resolvePlaceholders(locations[i])).trim();
        }
    }

    /**
     * accepts delimited list from {@link setConfigLocation}
     */
    public void setConfigLocations(String[] list) {
        configLocations = list;
    }

    /**
     * list of instantiated nested contexts
     */
    public List<ConfigurableApplicationContext> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public Void export(ExportRef ref) {
        if (log.isDebugEnabled()) {
            log.debug("exporting bean '" + ref.getTarget() + "' with interface '" + ref.getInterfaceClass().getSimpleName() + "'");
        }

        String singletonBeanName = ref.getTarget() + TARGET_SOURCE_SUFFIX;

        if (!context.containsBean(singletonBeanName)) {
            ExportLazyInitTargetSource exportLazyInitTargetSource = new ExportLazyInitTargetSource();
            exportLazyInitTargetSource.setTargetBeanName(ref.getTarget());
            exportLazyInitTargetSource.setBeanFactory(ref.getBeanFactory());
            exportLazyInitTargetSource.setTargetClass(ref.getInterfaceClass());
            exportLazyInitTargetSource.setExportClass(ref.getInterfaceClass());

            ((AbstractApplicationContext) context).getBeanFactory()
                    .registerSingleton(singletonBeanName, exportLazyInitTargetSource);
        } else {
            ExportLazyInitTargetSource exportLazyInitTargetSource = (ExportLazyInitTargetSource) context.getBean(singletonBeanName, TargetSource.class);
            exportLazyInitTargetSource.setBeanFactory(ref.getBeanFactory());
            exportLazyInitTargetSource.setExportClass(ref.getInterfaceClass());
        }

        return null;
//        return registry.export(ref);
    }

    @SuppressWarnings("unchecked")
    public <T> T lookup(String name, Class<T> clazz) {
        if (log.isDebugEnabled()) {
            log.debug("looking up bean '" + name + "' with interface '" + clazz.getSimpleName() + "'");
        }

        String beanDefinitionName = name + BEAN_DEF_SUFFIX;
        String singletonBeanName = name + TARGET_SOURCE_SUFFIX;

        if (context.containsBean(beanDefinitionName)) {
            return context.getBean(beanDefinitionName, clazz);
        } else {
            ExportLazyInitTargetSource exportLazyInitTargetSource = new ExportLazyInitTargetSource();
            exportLazyInitTargetSource.setTargetBeanName(name);
            exportLazyInitTargetSource.setTargetClass(clazz);

            ((AbstractApplicationContext) context).getBeanFactory()
                    .registerSingleton(singletonBeanName, exportLazyInitTargetSource);

            ConfigurableBeanFactory factory = ((AbstractApplicationContext) context).getBeanFactory();
            ((BeanDefinitionRegistry) factory).registerBeanDefinition(beanDefinitionName,
                    BeanDefinitionBuilder.genericBeanDefinition(clazz).getBeanDefinition());

            return context.getBean(beanDefinitionName, clazz);
        }
//        return registry.lookup(name, clazz);
    }

    public <T> Collection<String> lookupByInterface(Class<T> clazz) {
        return registry.lookupByInterface(clazz);
    }

    public void destroy() throws Exception {
        Collections.reverse(children);
        for (ConfigurableApplicationContext child : children) {
            child.close();
        }
    }

    public String[] getConfigLocations() {
        return configLocations;
    }
}