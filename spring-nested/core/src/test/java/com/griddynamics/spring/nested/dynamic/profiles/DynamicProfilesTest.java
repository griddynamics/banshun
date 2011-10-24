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
        registry.setFireOnly(new String[] {"object1"});
        registry.afterPropertiesSet();

        assertEquals(new String[]{"/com/griddynamics/spring/nested/dynamic/profiles/ctx1.xml"}, registry.getConfigLocations());
    }

    @Test
    public void dynamicProfileTest1() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/spring/nested/dynamic/profiles/root-ctx.xml");
        StrictContextParentBean registry = (StrictContextParentBean) context.getBean("root");
        registry.setFireOnly(new String[] {"context3", "context7"});
        registry.afterPropertiesSet();

        assertEquals(new String[]{
                "/com/griddynamics/spring/nested/dynamic/profiles/ctx1.xml",
                "/com/griddynamics/spring/nested/dynamic/profiles/ctx2.xml",
                "/com/griddynamics/spring/nested/dynamic/profiles/ctx7.xml",
                "/com/griddynamics/spring/nested/dynamic/profiles/ctx3.xml"
        }, registry.getConfigLocations());
    }

    @Test
    public void dynamicProfileTest2() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/spring/nested/dynamic/profiles/root-ctx.xml");
        StrictContextParentBean registry = (StrictContextParentBean) context.getBean("root");
        registry.setFireOnly(new String[]{"context6"});
        registry.afterPropertiesSet();
        assertEquals(new String[]{
                "/com/griddynamics/spring/nested/dynamic/profiles/ctx1.xml",
                "/com/griddynamics/spring/nested/dynamic/profiles/ctx2.xml",
                "/com/griddynamics/spring/nested/dynamic/profiles/ctx7.xml",
                "/com/griddynamics/spring/nested/dynamic/profiles/ctx6.xml"
        }, registry.getConfigLocations());
    }

    @Test
    public void dynamicProfileTest3() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/spring/nested/dynamic/profiles/root-ctx.xml");
        StrictContextParentBean registry = (StrictContextParentBean) context.getBean("root");
        registry.setFireOnly(new String[] {"context5"});
        registry.afterPropertiesSet();

        assertEquals(new String[]{
                "/com/griddynamics/spring/nested/dynamic/profiles/ctx1.xml",
                "/com/griddynamics/spring/nested/dynamic/profiles/ctx2.xml",
                "/com/griddynamics/spring/nested/dynamic/profiles/ctx7.xml",
                "/com/griddynamics/spring/nested/dynamic/profiles/ctx3.xml",
                "/com/griddynamics/spring/nested/dynamic/profiles/ctx4.xml",
                "/com/griddynamics/spring/nested/dynamic/profiles/ctx6.xml",
                "/com/griddynamics/spring/nested/dynamic/profiles/ctx5.xml"
        }, registry.getConfigLocations());
    }
}
