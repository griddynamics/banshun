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

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.TargetSourceCreator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LookupTargetSourceCreator implements TargetSourceCreator, ApplicationContextAware {
    private ApplicationContext context;

    public TargetSource getTargetSource(Class<?> beanClass, String beanName) {
        if (!beanName.endsWith(ContextParentBean.BEAN_DEF_SUFFIX)) {
            return null;
        }

        final Pattern pattern = Pattern.compile("(.*)" + ContextParentBean.BEAN_DEF_SUFFIX);
        Matcher matcher = pattern.matcher(beanName);
        matcher.matches();

        String actualBeanName = matcher.group(1);
        String name = actualBeanName + ContextParentBean.TARGET_SOURCE_SUFFIX;

        return new LookupTargetSource(context, name, beanClass);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
