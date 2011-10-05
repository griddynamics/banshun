package com.griddynamics.spring.nested.web;

import java.util.List;
import java.util.Map;

import com.griddynamics.spring.nested.ContextParentBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

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
            registerHandlersFromContext(child);
        }
    }

    public void registerHandlersFromContext(ApplicationContext context) {
        for (String beanDefName : context.getBeanDefinitionNames()) {
            try {
                checkAndRegisterHandler(context.getBean(beanDefName), context);
                System.out.println(beanDefName);
            } catch (BeansException ex) {
                logger.info(ex.getMessage(), ex);
            }
        }
        registerHandlersFromSimpleUrlHandlerMappings(context);
    }

    public void checkAndRegisterHandler(Object object, ApplicationContext context) {
        if (isUrlAnnotatedBean(object)) {
            registerByAnnotation(object);
            return;
        }

        BeanNameUrlHandlerMapping beanNameMapping = new BeanNameUrlHandlerMapping();
        beanNameMapping.setApplicationContext(context);
        if (beanNameMapping.getHandlerMap().containsValue(object)) {
            for (String name : beanNameMapping.getHandlerMap().keySet()) {
                if (beanNameMapping.getHandlerMap().get(name).equals(object)) {
                    registerByName(name, object);
                    return;
                }
            }
        }
    }

    public void registerHandlersFromSimpleUrlHandlerMappings(ApplicationContext context) {
        Map<String, SimpleUrlHandlerMapping> handlerMappings = context.getBeansOfType(org.springframework.web.servlet.handler.SimpleUrlHandlerMapping.class);
        for (String name : handlerMappings.keySet()) {
            SimpleUrlHandlerMapping handlerMapping = handlerMappings.get(name);
            for (String url : handlerMapping.getUrlMap().keySet()) {
                registerByName(url, context.getBean((String) handlerMapping.getUrlMap().get(url)));
            }
        }
    }
}
