package com.griddynamics.spring.nested;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.TargetSourceCreator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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

public class NestedTargetSourceCreator implements TargetSourceCreator, ApplicationContextAware {
    private ApplicationContext context;

    public TargetSource getTargetSource(Class beanClass, String beanName) {
        if (!beanName.contains(ContextParentBean.BEAN_DEF_SUFFIX)) {
            return null;
        }

        try {
            String actualBeanName = beanName.split(ContextParentBean.BEAN_DEF_SUFFIX)[0];
            String name = actualBeanName + ContextParentBean.TARGET_SOURCE_SUFFIX;

            LookupTargetSource lts = new LookupTargetSource();
            lts.setApplicationContext(context);
            lts.setTargetBeanName(name);
            lts.setTargetClass(beanClass);

            return lts;
        } catch (BeansException ex) {
            return null;
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
