package com.griddynamics.banshun.web;

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
@SuppressWarnings("serial")
public class Servlet extends DispatcherServlet {
    public static final String springCtxAttrName = "springContextAttrName";

    public Servlet() {
        setContextAttribute(springCtxAttrName);
    }
}
