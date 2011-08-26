package com.macys.platform.util.spring.nested.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 14, 2010
 * Time: 8:54:05 AM
 */
public class NestedNamespaceHandler extends NamespaceHandlerSupport {
    public void init() {
        super.registerBeanDefinitionParser("export", new NestedBeanDefinitionParser() );
        super.registerBeanDefinitionParser("import", new NestedBeanDefinitionParser() );
    }
}
