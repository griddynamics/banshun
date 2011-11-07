package com.griddynamics.spring.nested;

import org.springframework.aop.TargetSource;
import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
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
public class LookupTargetSource extends AbstractBeanFactoryBasedTargetSource {
    private Object target;
    private ApplicationContext context;
    private ExportTargetSource ets = null;

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Object getTarget() throws BeansException {
        String actualBeanName = getTargetBeanName().split(ContextParentBean.TARGET_SOURCE_SUFFIX)[0];

        if (context.containsBean(getTargetBeanName())) {
            ets = (ExportTargetSource) context.getBean(getTargetBeanName(), TargetSource.class);
            checkForCorrectAssignment(ets.getTargetClass(), actualBeanName);
            if (target == null) {
                this.target = ets.getTarget();
            }
            return this.target;
        } else {
            throw new NoSuchBeanDefinitionException(actualBeanName, "can't find export declaration for lookup("
                    + actualBeanName + "," + getTargetClass() + ")");
        }
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
