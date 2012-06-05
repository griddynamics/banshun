package com.griddynamics.banshun.xml;

import com.griddynamics.banshun.Registry;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
 * Time: 9:23:53 AM
 */
public class NestedTest {

    private static Registry registry;

    @BeforeClass
    public static void before() {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/banshun/xml/root-context.xml");
        registry = (Registry) context.getBean("root");
    }

    @Test
    public void testNested() {
        Child first = registry.lookup("firstChild", Child.class);
        Child second = registry.lookup("secondChild", Child.class);

        assertNotNull(first);
        assertEquals("firstChild", first.getName() );

        assertNotNull(second);
        assertEquals("secondChild", second.getName() );
    }

    @Test
    public void testChildWithChild() {
        Parent parent = registry.lookup("parent", Parent.class);

        assertNotNull(parent);
        assertNotNull(parent.getChild());
        assertEquals("secondChild", parent.getChild().getName() );
    }
}
