package com.griddynamics.spring.nested;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;

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
 * @Description:
 *
 */
public final class SingleResourceXmlChildContext extends ClassPathXmlApplicationContext {
    private Resource res;

    public SingleResourceXmlChildContext(Resource res, ApplicationContext parent) {
        this.res = res;
        setParent(parent);
        refresh();
    }

    protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
        reader.loadBeanDefinitions(res);
    }
}
