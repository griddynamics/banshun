package com.griddynamics.banshun.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.griddynamics.banshun.ContextParentBean;
import com.griddynamics.banshun.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.OrderComparator;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

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
 * @Description:
 */
public class ScanChildrenHandlerMapping extends ContextParentAnnotationHandlerMapping implements ApplicationListener<ApplicationEvent> {
    @SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(ContextParentBean.class);
    private static final Class<?>[] defaultHandlerMappingClasses = new Class[]{
    	org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping.class,
    	org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping.class};
    private Registry parentBean = null;

    public void setParentBean(Registry parentBean) {
        this.parentBean = parentBean;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            scanChildContexts();
        }
    }

    public void scanChildContexts() {
        List<ConfigurableApplicationContext> children = ((ContextParentBean) parentBean).getChildren();
        for (ConfigurableApplicationContext child : children) {
            createHandlerMappingsAndRegisterHandlers(child);
        }
    }

    public void createHandlerMappingsAndRegisterHandlers(ApplicationContext child) {
        Map<String, HandlerMapping> matchingBeans =
                BeanFactoryUtils.beansOfTypeIncludingAncestors(child, HandlerMapping.class, true, false);

        List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>(matchingBeans.values());
        OrderComparator.sort(handlerMappings);
        handlerMappings.remove(this);
        if (handlerMappings.isEmpty()) {
            handlerMappings = createDefaultHandlerMappings(child);
            if (logger.isDebugEnabled()) {
                logger.debug("No HandlerMappings found in context '" + child.getDisplayName() + "': using default");
            }
        }
        registerHandlers(handlerMappings);
    }

    private void registerHandlers(List<HandlerMapping> handlerMappings) {
        for (HandlerMapping mapping : handlerMappings) {
            AbstractUrlHandlerMapping abstractUrlHandlerMapping = (AbstractUrlHandlerMapping) mapping;
            Map<String, Object> handlerMap = abstractUrlHandlerMapping.getHandlerMap();
            for (String url : handlerMap.keySet()) {
                Object handler = handlerMap.get(url);
                registerHandler(url, handler);
            }
        }
    }

    private List<HandlerMapping> createDefaultHandlerMappings(ApplicationContext context) {
        List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>(2);
        try {
            for (Class<?> clazz : defaultHandlerMappingClasses) {
                Object handlerMapping =
                        context.getAutowireCapableBeanFactory().createBean(clazz);
                handlerMappings.add((HandlerMapping) handlerMapping);
            }
        } catch (Exception ex) {
            throw new BeanInitializationException(ex.getMessage(), ex);
        }

        return handlerMappings;
    }
}
