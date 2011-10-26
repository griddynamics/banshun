package com.griddynamics.spring.nested.wildcards;

import com.griddynamics.spring.nested.ContextParentBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WildcardsTest {
    private ContextParentBean registry = null;

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/spring/nested/wildcards/root-ctx.xml");
        registry = (ContextParentBean) context.getBean("root");
    }

    @Test
    public void wildcardsTest0() throws Exception {
        registry.setConfigLocation(
                "/com/griddynamics/spring/nested/wildcards/*7.xml"
        );

        assertEquals(1, registry.getConfigLocations().length);
        assertTrue(registry.getConfigLocations()[0].contains("ctx7.xml"));
    }

    @Test
    public void wildcardsTest1() throws Exception {
        registry.setConfigLocation(
                "/com/griddynamics/spring/nested/wildcards/sub1/ctx1.xml\n" +
                "/com/griddynamics/spring/nested/wildcards/sub2/*.xml\n" +
                "/com/griddynamics/spring/nested/wildcards/sub1/*.xml\n" +
                "/com/griddynamics/spring/nested/wildcards/sub2/ctx6.xml\n" +
                "/com/griddynamics/spring/nested/wildcards/ctx*.xml"
        );

        assertEquals(8, registry.getConfigLocations().length);
        assertTrue(registry.getConfigLocations()[0].contains("ctx1.xml"));
        assertTrue(registry.getConfigLocations()[1].contains("ctx4.xml"));
        assertTrue(registry.getConfigLocations()[2].contains("ctx2.xml"));
        assertTrue(registry.getConfigLocations()[3].contains("ctx3.xml"));
        assertTrue(registry.getConfigLocations()[4].contains("ctx5.xml"));
        assertTrue(registry.getConfigLocations()[5].contains("ctx6.xml"));
        assertTrue(registry.getConfigLocations()[6].contains("ctx8.xml"));
        assertTrue(registry.getConfigLocations()[7].contains("ctx7.xml"));
    }

    @Test
    public void wildcardsTest2() throws Exception {
        registry.setConfigLocation(
                "classpath:/com/griddynamics/spring/nested/wildcards/sub1/ctx1.xml\n" +
                "/com/griddynamics/spring/nested/wildcards/sub1/ctx1.xml\n" +
                "classpath:/com/griddynamics/spring/nested/wildcards/sub1/ctx1.xml\n" +
                "classpath:/com/griddynamics/spring/nested/wildcards/sub1/ctx1*.xml\n"
        );

        assertEquals(1, registry.getConfigLocations().length);
        assertTrue(registry.getConfigLocations()[0].contains("ctx1.xml"));
    }
}
