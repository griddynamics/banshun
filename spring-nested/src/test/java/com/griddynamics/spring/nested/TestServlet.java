package com.griddynamics.spring.nested;

import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class TestServlet extends DispatcherServlet {
    @Override
    protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
        wac.setConfigLocation("classpath:/com/griddynamics/spring/nested/controllers-test/parent-context.xml");
    }
}
