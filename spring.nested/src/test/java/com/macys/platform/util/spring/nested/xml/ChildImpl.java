package com.macys.platform.util.spring.nested.xml;

import org.springframework.beans.factory.BeanNameAware;

/**
 * Created by IntelliJ IDEA.
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
