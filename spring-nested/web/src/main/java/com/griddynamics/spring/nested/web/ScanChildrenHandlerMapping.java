package com.griddynamics.spring.nested.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.griddynamics.spring.nested.ContextParentBean;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
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
public class ScanChildrenHandlerMapping extends ContextParentAnnotationHandlerMapping implements InitializingBean {
    public boolean isUrlAnnotatedBean(Object object) {
        return determineUrlsByAnnotations(object) != null;
    }

    public void afterPropertiesSet() throws Exception {
        ApplicationContext context = getApplicationContext();
        ContextParentBean parentBean = context.getBean(com.griddynamics.spring.nested.ContextParentBean.class);
        List<ConfigurableApplicationContext> children = parentBean.getChildren();
        for (ConfigurableApplicationContext child : children) {
            getHandlerMappingsAndRegisterHandlers(child);
        }
    }

    public void getHandlerMappingsAndRegisterHandlers(ApplicationContext context) {
        Map<String, HandlerMapping> matchingBeans =
                BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);

        List<HandlerMapping> handlerMappings = null;
        if (!matchingBeans.isEmpty()) {
            handlerMappings = new ArrayList<HandlerMapping>(matchingBeans.values());
            OrderComparator.sort(handlerMappings);
            registerHandlers(handlerMappings);
        }

        if (handlerMappings == null || handlerMappings.size() == 1) {
            handlerMappings = getDefaultHandlerMappings(context);
            if (logger.isDebugEnabled()) {
                logger.debug("No HandlerMappings found in context '" + context.getDisplayName() + "': using default");
            }
            registerHandlers(handlerMappings);
        }
    }

    private void registerHandlers(List<HandlerMapping> handlerMappings) {
        for (HandlerMapping mapping : handlerMappings) {
            AbstractUrlHandlerMapping abstractUrlHandlerMapping = (AbstractUrlHandlerMapping) mapping;
            Map<String, Object> handlerMap = abstractUrlHandlerMapping.getHandlerMap();
            for (String beanName : handlerMap.keySet()) {
                Object handler = handlerMap.get(beanName);
                if (isUrlAnnotatedBean(handler)) {
                    registerByAnnotation(handler);
                } else {
                    registerByName(beanName, handler);
                }
            }
        }
    }

    private List<HandlerMapping> getDefaultHandlerMappings(ApplicationContext context) {
        List<HandlerMapping> handlerMappings = new ArrayList<HandlerMapping>(2);
        try {
            Object handlerMapping =
                    context.getAutowireCapableBeanFactory().createBean(org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping.class);
            handlerMappings.add((HandlerMapping) handlerMapping);

            handlerMapping =
                    context.getAutowireCapableBeanFactory().createBean(org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping.class);
            handlerMappings.add((HandlerMapping) handlerMapping);
        } catch (Exception ex) {
            throw new BeanInitializationException(ex.getMessage(), ex);
        }

        return handlerMappings;
    }
}
