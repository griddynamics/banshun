package com.griddynamics.spring.nested;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.TargetSourceCreator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class LookupTargetSourceCreator implements TargetSourceCreator, ApplicationContextAware {
    private ApplicationContext context;

    public TargetSource getTargetSource(Class beanClass, String beanName) {
        if (!beanName.endsWith(ContextParentBean.BEAN_DEF_SUFFIX)) {
            return null;
        }

        final Pattern pattern = Pattern.compile("(.*)" + ContextParentBean.BEAN_DEF_SUFFIX);
        Matcher matcher = pattern.matcher(beanName);
        matcher.matches();

        String actualBeanName = matcher.group(1);
        String name = actualBeanName + ContextParentBean.TARGET_SOURCE_SUFFIX;

        LookupTargetSource lts = new LookupTargetSource(context, name, beanClass);

        return lts;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
