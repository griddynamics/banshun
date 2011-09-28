package com.griddynamics.spring.nested;

/**
 * Copyright (c) 2011 Grid Dynamics Consulting Services, Inc, All Rights
 * Reserved http://www.griddynamics.com
 * 
 * For information about the licensing and copyright of this document please
 * contact Grid Dynamics at info@griddynamics.com.
 * 
 * $Id: $
 * 
 * @Project: Spring Nested
 * @Description: 
 * 
 */
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