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
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    public boolean isStatic() {
        return false;
    }

    public void releaseTarget(Object target) throws Exception {
    }

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
                    log.warn("Bean {} was created earlier", actualBeanName);
                    return target.get();
                }
            } else {
                throw new NoSuchBeanDefinitionException(actualBeanName, String.format(
                        "can't find export declaration for lookup(%s, %s)", actualBeanName, getTargetClass()));
            }
        }
        return localTarget;
    }

    private void checkForCorrectAssignment(Class<?> exportClass, String actualBeanName, Class<?> actualBeanClass) {
        if (!getTargetClass().isAssignableFrom(exportClass)) {
            throw new BeanNotOfRequiredTypeException(actualBeanName, getTargetClass(), exportClass);
        }

        if (!exportClass.isAssignableFrom(actualBeanClass)) {
            throw new BeanCreationException(actualBeanName,
                    new BeanNotOfRequiredTypeException(actualBeanName, actualBeanClass, exportClass));
        }
    }
}
