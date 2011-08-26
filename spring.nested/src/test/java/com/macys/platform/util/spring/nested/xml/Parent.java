package com.macys.platform.util.spring.nested.xml;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 14, 2010
 * Time: 6:27:19 PM
 */
public interface Parent {

    Child getChild();

    void setChild(Child child);
}
