package com.griddynamics.spring.nested;

import org.springframework.aop.TargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;

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
public class LookupTargetSource implements TargetSource {
    private volatile Object target;
    private ApplicationContext context;
    private ExportTargetSource ets = null;

    private String targetBeanName;
    private String actualBeanName;
    private Class<?> targetClass;

    public void setTargetBeanName(String targetBeanName) {
        actualBeanName = targetBeanName.split(ContextParentBean.TARGET_SOURCE_SUFFIX)[0];
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

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public boolean isStatic() {
        return false;
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
                    if (context.containsBean(getTargetBeanName())) {
                        ets = (ExportTargetSource) context.getBean(getTargetBeanName(), TargetSource.class);
                        checkForCorrectAssignment(ets.getTargetClass(), actualBeanName);
                        target = ets.getTarget();
                    } else {
                        throw new NoSuchBeanDefinitionException(actualBeanName, "can't find export declaration for lookup("
                                + actualBeanName + "," + getTargetClass() + ")");
                    }
                }
            }
        }

        return target;
    }

    private void checkForCorrectAssignment(Class exportClass, String actualBeanName) {
        if (!getTargetClass().isAssignableFrom(exportClass)) {
            throw new BeanNotOfRequiredTypeException(actualBeanName, getTargetClass(), exportClass);
        }

        Class actualBeanClass = ets.getBeanFactory().getType(actualBeanName);
        if (!exportClass.isAssignableFrom(actualBeanClass)) {
            throw new BeanCreationException(actualBeanName, new BeanNotOfRequiredTypeException(actualBeanName, actualBeanClass, exportClass));
        }
    }
}
