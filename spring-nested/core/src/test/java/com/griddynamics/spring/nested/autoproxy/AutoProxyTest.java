package com.griddynamics.spring.nested.autoproxy;

import com.griddynamics.spring.nested.Registry;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AutoProxyTest {
    @Test
    public void testAutoProxy() {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/spring/nested/autoproxy/root-context.xml");
        Registry registry = (Registry) context.getBean("root");
        CustomerService customer = registry.lookup("customer", CustomerService.class);

        Assert.assertEquals("AroundMethod: Customer Name", customer.getName());
    }
}
