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

public class BeanReferenceInfo {

    private String beanName;
    private Class<?> beanInterface;
    private String location;

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanInterface(Class<?> beanInterface) {
        this.beanInterface = beanInterface;
    }

    public Class<?> getBeanInterface() {
        return beanInterface;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BeanReferenceInfo that = (BeanReferenceInfo) o;

        if (!beanInterface.equals(that.beanInterface)) return false;
        if (!beanName.equals(that.beanName)) return false;
        if (!location.equals(that.location)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = beanName.hashCode();
        result = 31 * result + beanInterface.hashCode();
        result = 31 * result + location.hashCode();
        return result;
    }
}