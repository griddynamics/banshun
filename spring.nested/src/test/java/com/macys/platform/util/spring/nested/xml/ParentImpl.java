package com.macys.platform.util.spring.nested.xml;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 14, 2010
 * Time: 6:28:21 PM
 */
public class ParentImpl implements Parent {

    private Child child;

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }
}
