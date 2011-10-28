package com.griddynamics.spring.nested.dynamic.profiles;

import com.griddynamics.spring.nested.StrictContextParentBean;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;

import static org.junit.Assert.*;

public class DynamicProfilesTest {
    @Test
    public void dynamicProfileTest0() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/spring/nested/dynamic/profiles/root-ctx.xml");
        StrictContextParentBean registry = (StrictContextParentBean) context.getBean("root");
        registry.setRunOnlyServices(new String[] {"object1"});
        registry.afterPropertiesSet();

        assertEquals(1, registry.getConfigLocations().length);
        assertTrue(registry.getConfigLocations()[0].contains("ctx1.xml"));
    }

    @Test
    public void dynamicProfileTest1() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/spring/nested/dynamic/profiles/root-ctx.xml");
        StrictContextParentBean registry = (StrictContextParentBean) context.getBean("root");
        registry.setRunOnlyServices(new String[] {"context3", "context7"});
        registry.afterPropertiesSet();

        assertEquals(4, registry.getConfigLocations().length);
        assertTrue(registry.getConfigLocations()[0].contains("ctx1.xml"));
        assertTrue(registry.getConfigLocations()[1].contains("ctx2.xml"));
        assertTrue(registry.getConfigLocations()[2].contains("ctx7.xml"));
        assertTrue(registry.getConfigLocations()[3].contains("ctx3.xml"));
    }

    @Test
    public void dynamicProfileTest2() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/spring/nested/dynamic/profiles/root-ctx.xml");
        StrictContextParentBean registry = (StrictContextParentBean) context.getBean("root");
        registry.setRunOnlyServices(new String[]{"context6"});
        registry.afterPropertiesSet();

        assertEquals(4, registry.getConfigLocations().length);
        assertTrue(registry.getConfigLocations()[0].contains("ctx1.xml"));
        assertTrue(registry.getConfigLocations()[1].contains("ctx2.xml"));
        assertTrue(registry.getConfigLocations()[2].contains("ctx7.xml"));
        assertTrue(registry.getConfigLocations()[3].contains("ctx6.xml"));
    }

    @Test
    public void dynamicProfileTest3() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/spring/nested/dynamic/profiles/root-ctx.xml");
        StrictContextParentBean registry = (StrictContextParentBean) context.getBean("root");
        registry.setRunOnlyServices(new String[] {"context5"});
        registry.afterPropertiesSet();

        assertEquals(7, registry.getConfigLocations().length);
        assertTrue(registry.getConfigLocations()[0].contains("ctx1.xml"));
        assertTrue(registry.getConfigLocations()[1].contains("ctx2.xml"));
        assertTrue(registry.getConfigLocations()[2].contains("ctx7.xml"));
        assertTrue(registry.getConfigLocations()[3].contains("ctx3.xml"));
        assertTrue(registry.getConfigLocations()[4].contains("ctx4.xml"));
        assertTrue(registry.getConfigLocations()[5].contains("ctx6.xml"));
        assertTrue(registry.getConfigLocations()[6].contains("ctx5.xml"));
    }
}
