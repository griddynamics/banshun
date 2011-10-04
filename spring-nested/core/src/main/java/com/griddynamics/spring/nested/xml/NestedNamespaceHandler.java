package com.griddynamics.spring.nested.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
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
