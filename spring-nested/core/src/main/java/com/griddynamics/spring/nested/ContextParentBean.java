package com.griddynamics.spring.nested;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.io.IOException;
import java.util.*;

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
 * Also, it instantiate the nested children contexts by the given resources. These contexts
 * receives factory bean instantiates this bean as a "parent bean". This bean will be available via
 * this Spring intrinsic feature, by the some well known name.
 * Note: children contexts extends {@link XmlWebApplicationContext}. so it's a little bit straightforward and
 * intended for the current usage
 */
public class ContextParentBean implements InitializingBean, ApplicationContextAware, Registry, DisposableBean
        , ApplicationListener, ExceptionsLogger {
    private static final Logger log = LoggerFactory.getLogger(ContextParentBean.class);
    private Map<String, Exception> nestedContextsExceptions = new LinkedHashMap<String, Exception>();

    protected ApplicationContext context;
    private List<ConfigurableApplicationContext> children = new ArrayList<ConfigurableApplicationContext>();

    protected String[] configLocations = new String[0];
    protected List<String> resultConfigLocations;
    protected List<String> excludeConfigLocations = new ArrayList<String>();
    protected Set<String> ignoredLocations = new HashSet<String>();

    private boolean strictErrorHandling = false;
    private String childContextPrototype = null;

    public static final String TARGET_SOURCE_SUFFIX = "_targetSource";
    public static final String BEAN_DEF_SUFFIX = "_beanDef";
    public static final String EXPORT_REF_SUFFIX = "-export-ref";

    /**
     * specifies whether initialization of this bean failed if one of the nested children contexts
     * failed to build.
     *
     * @default false
     */
    public void setStrictErrorHandling(boolean strictErrorHandling) {
        this.strictErrorHandling = strictErrorHandling;
    }

    public void setChildContextPrototype(String childContextPrototype) {
        this.childContextPrototype = childContextPrototype;
    }

    public void setExcludeConfigLocations(String[] excludeConfigLocations) {
        this.excludeConfigLocations = Arrays.asList(excludeConfigLocations);
    }

    public List<String> getResultConfigLocations() {
        return resultConfigLocations;
    }

    /**
     * resolves configs paths and build nested children contexts
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<String> configLocations = new ArrayList<String>();
        List<String> resolvedConfigLocations = resolveConfigLocations(configLocations);
        List<String> narrowedConfigLocations = excludeConfigLocations(resolvedConfigLocations);
        this.resultConfigLocations = analyzeDependencies(narrowedConfigLocations);
    }

    protected List<String> analyzeDependencies(List<String> configLocations) throws Exception {
        return configLocations;
    }

    private void initializeChildContexts() {
        for (String loc : resultConfigLocations) {
            if (ignoredLocations.contains(loc)) {
                continue;
            }
            try {
                Resource[] resources = context.getResources(loc);

                for (final Resource res : resources) {
                    try {
                        ConfigurableApplicationContext child = createChildContext(res, context);
                        children.add(child);
                    } catch (Exception e) {
                        log.error(String.format("Failed to process resource [%s] from location [%s] ", res.getURL(), loc), e);
                        if (strictErrorHandling) {
                            throw new RuntimeException(e);
                        }
                        nestedContextsExceptions.put(loc, e);
                        addToFailedLocations(loc);
                        break;
                    }
                }
            } catch (IOException e) {
                log.error(String.format("Failed to process configuration from [%s]", loc), e);
                if (strictErrorHandling) {
                    throw new RuntimeException(e);
                }
                addToFailedLocations(loc);
            }
        }
    }

    private List<String> collectConfigLocations(String location) throws IOException {
        List<String> result = new ArrayList<String>();
        Resource[] resources = context.getResources(location);
        for (Resource resource : resources) {
            result.add(resource.getURI().toString());
        }
        return result;
    }

    protected List<String> resolveConfigLocations(List<String> configLocations) throws Exception {
        PathMatchingResourcePatternResolver pmrpr = new PathMatchingResourcePatternResolver();
        for (String loc : this.configLocations) {
            String location = loc;
            boolean wildcard = pmrpr.getPathMatcher().isPattern(location);
            List<String> collectedLocations = collectConfigLocations(location);
            for (String locName : collectedLocations) {
                if (!configLocations.contains(locName) && wildcard) {
                    configLocations.add(locName);
                }
                if (!wildcard) {
                    configLocations.remove(locName);
                    configLocations.add(locName);
                }
            }
        }

        log.info("resolved locations: " + configLocations);

        return configLocations;
    }

    protected List<String> excludeConfigLocations(List<String> configLocations) throws Exception {
        for (String loc : excludeConfigLocations) {
            String location = loc;
            configLocations.removeAll(collectConfigLocations(location));
        }
        return configLocations;
    }

    protected void addToFailedLocations(String loc) {
    }

    public Set<String> getIgnoredLocations() {
        return ignoredLocations;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            if (context.equals(((ContextRefreshedEvent) event).getApplicationContext())) {
                initializeChildContexts();
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

    @Override
    public Map<String, Exception> getNestedContextsExceptions() {
        return nestedContextsExceptions;
    }

    public void setApplicationContext(ApplicationContext arg0)
            throws BeansException {
        context = arg0;
    }

    /**
     * delimiter separated list of Spring-usual resources specifies. classpath*: classpath:, file:
     * start wildcards are supported.
     * delimiters are {@link ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS}
     */
    public void setConfigLocations(String[] locations) throws Exception {
        Assert.noNullElements(locations, "Config locations must not be null");

        configLocations = locations;
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
            ExportTargetSource exportTargetSource = new ExportTargetSource(ref.getTarget(), ref.getInterfaceClass(), ref.getBeanFactory());

            ((AbstractApplicationContext) context).getBeanFactory()
                    .registerSingleton(singletonBeanName, exportTargetSource);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T lookup(String name, Class<T> clazz) {
        if (log.isDebugEnabled()) {
            log.debug("looking up bean '" + name + "' with interface '" + clazz.getSimpleName() + "'");
        }

        String beanDefinitionName = name + BEAN_DEF_SUFFIX;

        if (!context.containsBean(beanDefinitionName)) {
            ConfigurableBeanFactory factory = ((AbstractApplicationContext) context).getBeanFactory();
            ((BeanDefinitionRegistry) factory).registerBeanDefinition(beanDefinitionName,
                    BeanDefinitionBuilder.genericBeanDefinition(clazz).getBeanDefinition());
        }

        return context.getBean(beanDefinitionName, clazz);
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