package com.griddynamics.spring.nested;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
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
 * 
 * For information about the licensing and copyright of this document please
 * contact Grid Dynamics at info@griddynamics.com.
 * 
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
public class ContextParentBean implements InitializingBean, ApplicationContextAware, Registry, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ContextParentBean.class);
    private Map<String, Exception> nestedContextsExceptions = new LinkedHashMap<String, Exception>();

    protected ApplicationContext context;
    private List<ConfigurableApplicationContext> children = new ArrayList<ConfigurableApplicationContext>();

    private String[] configLocations;
    private Registry registry;

    private boolean strictErrorHandling = false;
    private String childApplicationContextClassName = null;

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

    public void setChildApplicationContextClassName(String childApplicationContextClassName) {
        this.childApplicationContextClassName = childApplicationContextClassName;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
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

    private ConfigurableApplicationContext createChildContext(Resource res, ApplicationContext parent) throws Exception {
        if (childApplicationContextClassName != null && childApplicationContextClassName.length() > 0) {
            try {
                return (ConfigurableApplicationContext) parent.getBean(childApplicationContextClassName, res, parent);
            } catch (Exception e) {
                log.warn("Can not initialize ApplicationContext " + childApplicationContextClassName + " with configuration location " + res.getURL(), e);
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
        return registry.export(ref);
    }

    public <T> T lookup(String name, Class<T> clazz) {
        if (log.isDebugEnabled()) {
            log.debug("looking up bean '" + name + "' with interface '" + clazz.getSimpleName() + "'");
        }
        return registry.lookup(name, clazz);
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