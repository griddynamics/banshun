/**
 *    Copyright 2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 *    http://www.griddynamics.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *  @Project: Banshun
 * */
package com.griddynamics.banshun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicReference;

public class LookupTargetSource implements TargetSource {

    private static final Logger log = LoggerFactory.getLogger(LookupTargetSource.class);

    private final AtomicReference<Object> target = new AtomicReference<Object>();
    private final String actualBeanName;
    private final String targetBeanName;
    private final Class<?> targetClass;
    private final ApplicationContext context;


    public LookupTargetSource(String actualBeanName, String targetBeanName, Class<?> targetClass, ApplicationContext context) {
        this.actualBeanName = actualBeanName;
        this.targetBeanName = targetBeanName;
        this.targetClass = targetClass;
        this.context = context;
    }


    public Class<?> getTargetClass() {
        return targetClass;
    }

    public boolean isStatic() {
        return false;
    }

    public void releaseTarget(Object target) {
    }

    public Object getTarget() throws BeansException {
        Object localTarget = target.get();

        if (localTarget == null) {
            if (context.containsBean(targetBeanName)) {
                ExportTargetSource targetSource = context.getBean(targetBeanName, ExportTargetSource.class);

                checkForCorrectAssignment(targetSource.getTargetClass(), targetSource.getBeanFactory().getType(actualBeanName));

                if (target.compareAndSet(null, localTarget = targetSource.getTarget())) {
                    return localTarget;
                } else {
                    // log potentially redundant instance initialization
                    log.warn("Bean {} was created earlier", actualBeanName);
                    return target.get();
                }
            } else {
                throw new NoSuchBeanDefinitionException(actualBeanName, String.format(
                        "Can't find export declaration for lookup(%s, %s)", actualBeanName, targetClass));
            }
        }
        return localTarget;
    }

    private void checkForCorrectAssignment(Class<?> exportClass, Class<?> actualBeanClass) {
        if (!targetClass.isAssignableFrom(exportClass)) {
            throw new BeanNotOfRequiredTypeException(actualBeanName, targetClass, exportClass);
        }

        if (!exportClass.isAssignableFrom(actualBeanClass)) {
            throw new BeanCreationException(actualBeanName,
                    new BeanNotOfRequiredTypeException(actualBeanName, actualBeanClass, exportClass));
        }
    }
}
