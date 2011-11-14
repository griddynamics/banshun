package com.griddynamics.spring.nested;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

import java.util.concurrent.atomic.AtomicReference;

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
    private static final Logger log = LoggerFactory.getLogger(ContextParentBean.class);

    private final AtomicReference<Object> target = new AtomicReference<Object>();
    private final String targetBeanName;
    private final Class<?> targetClass;
    private final BeanFactory beanFactory;


    public ExportTargetSource(String targetBeanName, Class<?> targetClass, BeanFactory beanFactory) {
        this.targetBeanName = targetBeanName;
        this.targetClass = targetClass;
        this.beanFactory = beanFactory;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public String getTargetBeanName() {
        return this.targetBeanName;
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
        Object localTarget = target.get();
        if (localTarget == null) {
            if (target.compareAndSet(null, localTarget = getBeanFactory().getBean(getTargetBeanName()))) {
                return localTarget;
            } else {
                // log potentially redundant instance initialization
                log.warn("Bean " + targetBeanName + "were created earlier");
                return target.get();
            }
        }
        return localTarget;
    }
}
