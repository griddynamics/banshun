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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class ExportRef implements BeanFactoryAware {

    private String target;
    private Class<?> interfaceClass;
    private BeanFactory beanFactory;

    public ExportRef() {
    }

    public ExportRef(String target) {
        this.target = target;
    }

    public ExportRef(String target, Class<?> interfaceClass) {
        this.target = target;
        this.interfaceClass = interfaceClass;
    }

    public void setTarget(String arg0) {
        target = arg0;
    }

    /**
     * Used by Spring to inject beanFactory which will be used to resolve reference
     */
    public void setBeanFactory(BeanFactory arg0) throws BeansException {
        beanFactory = arg0;
    }

    /**
     * name of the exported service. used for find this export reference by key.
     * also it used for find the actual service bean in injected bean factory.
     * <idref> is useful to inject the bean name to this field.
     */
    public String getTarget() {
        return target;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * constraint for this reference. requested lookup calls should specify the same
     * interface
     */
    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public String toString() {
        return "ExportRef [target=" + target + ", interfaceClass="
                + interfaceClass + ", beanFactory=" + beanFactory + "]";
    }
}
