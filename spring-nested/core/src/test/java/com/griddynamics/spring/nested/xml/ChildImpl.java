package com.griddynamics.spring.nested.xml;

import org.springframework.beans.factory.BeanNameAware;

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
 * User: oleg
 * Date: Sep 14, 2010
 * Time: 9:52:40 AM
 */
public class ChildImpl implements BeanNameAware, Child {

    private String name;

    public void setBeanName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
}
