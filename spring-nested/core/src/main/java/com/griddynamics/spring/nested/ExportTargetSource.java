package com.griddynamics.spring.nested;

import org.springframework.aop.TargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

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
public class ExportTargetSource implements TargetSource {
    private volatile Object target;
    private String targetBeanName;
    private Class<?> targetClass;
    private BeanFactory beanFactory;

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    public String getTargetBeanName() {
        return this.targetBeanName;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public void releaseTarget(Object target) throws Exception {
    }

    @Override
    public Object getTarget() throws BeansException {
        Object localTarget = target;
        if (localTarget == null) {
            synchronized (this) {
                localTarget = target;
                if (localTarget == null) {
                    target = getBeanFactory().getBean(getTargetBeanName());
                }
            }
        }

        return target;
    }
}
