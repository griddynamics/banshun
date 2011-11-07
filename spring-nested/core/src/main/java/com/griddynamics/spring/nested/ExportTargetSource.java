package com.griddynamics.spring.nested;

import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;
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
public class ExportTargetSource extends AbstractBeanFactoryBasedTargetSource {
    private Object target;

    @Override
    public Object getTarget() throws BeansException {
        if (target == null) {
            this.target = getBeanFactory().getBean(getTargetBeanName());
        }
        return this.target;
    }
}
