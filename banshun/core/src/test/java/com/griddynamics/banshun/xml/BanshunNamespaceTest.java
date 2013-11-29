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
package com.griddynamics.banshun.xml;

import com.griddynamics.banshun.Registry;
import com.griddynamics.banshun.fixtures.Child;
import com.griddynamics.banshun.fixtures.Parent;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class BanshunNamespaceTest {

    private static Registry registry;

    @BeforeClass
    public static void initialize() {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/banshun/xml/root-context.xml");
        registry = (Registry) context.getBean("root");
    }

    @Test
    public void lookupExportedBeans() {
        Child first = registry.lookup("firstChild", Child.class);
        Child second = registry.lookup("secondChild", Child.class);

        assertNotNull(first);
        assertEquals("firstChild", first.getName() );

        assertNotNull(second);
        assertEquals("secondChild", second.getName() );
    }

    @Test
    public void lookupExportedBeanDependingOnImportedBean() {
        Parent parent = registry.lookup("parent", Parent.class);

        assertNotNull(parent);
        assertNotNull(parent.getChild());
        assertEquals("secondChild", parent.getChild().getName() );
    }
}
