package com.griddynamics.spring.nested.web;

import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

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
public class ContextServlet extends DispatcherServlet {
    @Override
    protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
        wac.setConfigLocation("classpath:/com/griddynamics/spring/nested/controllers-test/parent-context.xml");
    }
}
