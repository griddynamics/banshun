package com.griddynamics.banshun.autoproxy;

import com.griddynamics.banshun.Registry;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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

public class AutoProxyTest {
    @Test
    public void testAutoProxy() {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/banshun/autoproxy/root-context.xml");
        Registry registry = (Registry) context.getBean("root");
        CustomerService customer = registry.lookup("customer", CustomerService.class);

        Assert.assertEquals("AroundMethod: Customer Name", customer.getName());
    }
}
