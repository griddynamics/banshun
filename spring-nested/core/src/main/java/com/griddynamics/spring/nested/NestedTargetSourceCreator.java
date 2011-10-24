package com.griddynamics.spring.nested;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.TargetSourceCreator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.jar.Attributes;

public class NestedTargetSourceCreator implements TargetSourceCreator, ApplicationContextAware {
    private ApplicationContext context;


    public TargetSource getTargetSource(Class beanClass, String beanName) {
        if (!beanName.contains(ContextParentBean.BEAN_DEF_SUFFIX)) {
            return null;
        }

        try {
            String name = beanName.split(ContextParentBean.BEAN_DEF_SUFFIX)[0] + ContextParentBean.TARGET_SOURCE_SUFFIX;
            return (TargetSource) context.getBean(name);
        } catch (BeansException ex) {
            return null;
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
