package com.griddynamics.banshun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicReference;
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
public class LookupTargetSource implements TargetSource {
    private static final Logger log = LoggerFactory.getLogger(ContextParentBean.class);

    private AtomicReference<Object> target = new AtomicReference<Object>();
    private final ApplicationContext context;

    private final String targetBeanName;
    private String actualBeanName;
    private final Class<?> targetClass;

    public LookupTargetSource(ApplicationContext context, String targetBeanName, Class<?> targetClass) {
        this.context = context;
        this.targetBeanName = targetBeanName;
        this.targetClass = targetClass;

        final Pattern pattern = Pattern.compile("(.*)" + ContextParentBean.TARGET_SOURCE_SUFFIX);
        Matcher matcher = pattern.matcher(targetBeanName);
        matcher.matches();
        this.actualBeanName = matcher.group(1);
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
        return false;
    }

    @Override
    public void releaseTarget(Object target) throws Exception {
    }

    @Override
    public Object getTarget() throws BeansException {
        Object localTarget = target.get();
        if (localTarget == null) {
            if (context.containsBean(getTargetBeanName())) {
                ExportTargetSource ets = (ExportTargetSource) context.getBean(getTargetBeanName(), TargetSource.class);
                checkForCorrectAssignment(ets.getTargetClass(), actualBeanName, ets.getBeanFactory().getType(actualBeanName));
                if (target.compareAndSet(null, localTarget = ets.getTarget())) {
                    return localTarget;
                } else {
                    // log potentially redundant instance initialization
                    log.warn("Bean " + actualBeanName + "was created earlier");
                    return target.get();
                }
            } else {
                throw new NoSuchBeanDefinitionException(actualBeanName, "can't find export declaration for lookup("
                        + actualBeanName + "," + getTargetClass() + ")");
            }
        }
        return localTarget;
    }

    private void checkForCorrectAssignment(Class<?> exportClass, String actualBeanName, Class<?> actualBeanClass) {
        if (!getTargetClass().isAssignableFrom(exportClass)) {
            throw new BeanNotOfRequiredTypeException(actualBeanName, getTargetClass(), exportClass);
        }

        if (!exportClass.isAssignableFrom(actualBeanClass)) {
            throw new BeanCreationException(actualBeanName, new BeanNotOfRequiredTypeException(actualBeanName, actualBeanClass, exportClass));
        }
    }
}
