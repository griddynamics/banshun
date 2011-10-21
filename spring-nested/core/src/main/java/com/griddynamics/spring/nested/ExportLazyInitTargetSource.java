package com.griddynamics.spring.nested;

import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
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
public class ExportLazyInitTargetSource extends AbstractBeanFactoryBasedTargetSource {
    private Class exportClass = null;
    private Object target;

    public void setExportClass(Class exportClass) {
        this.exportClass = exportClass;
    }

    @Override
    public synchronized Object getTarget() throws BeansException {
        preProcess();
        if (target == null) {
            this.target = getBeanFactory().getBean(getTargetBeanName());
        }
        return this.target;
    }

    private void preProcess() {
        if (exportClass == null) {
            throw new NoSuchBeanDefinitionException(getTargetBeanName(), "can't find export declaration for lookup("
                    + getTargetBeanName() + "," + getTargetClass() + ")");
        } else {
            if (!getTargetClass().isAssignableFrom(exportClass)) {
                throw new BeanNotOfRequiredTypeException(getTargetBeanName(), getTargetClass(), exportClass);
            }

            Class actualBeanClass = getBeanFactory().getType(getTargetBeanName());
            if (!exportClass.isAssignableFrom(actualBeanClass)) {
                throw new BeanCreationException(getTargetBeanName(), new BeanNotOfRequiredTypeException(getTargetBeanName(), actualBeanClass, exportClass));
            }
        }
    }
}
