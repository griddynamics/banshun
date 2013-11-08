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
import org.springframework.beans.factory.BeanFactory;

import java.util.concurrent.atomic.AtomicReference;

public class ExportTargetSource implements TargetSource {
    private static final Logger log = LoggerFactory.getLogger(ExportTargetSource.class);

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
        return targetBeanName;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public boolean isStatic() {
        return true;
    }

    public void releaseTarget(Object target) {
    }

    public Object getTarget() throws BeansException {
        Object localTarget = target.get();

        if (localTarget == null) {
            if (target.compareAndSet(null, localTarget = getBeanFactory().getBean(getTargetBeanName()))) {
                return localTarget;
            } else {
                // log potentially redundant instance initialization
                log.info("Needles creation of bean {} caused by concurrency has been detected. Ignoring new instance.", targetBeanName);
                return target.get();
            }
        }
        return localTarget;
    }
}
