package com.griddynamics.spring.nested.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
@Controller
@RequestMapping("/*")
public class AnnotatedTestController {
    @RequestMapping("/annotation-test.html")
    public ModelAndView annotationTest()  {
        String message = "Hello Spring MVC";

        ModelAndView modelAndView = new ModelAndView("testView");
        modelAndView.addObject("message", message);

        return modelAndView;
    }

    @RequestMapping("/another-annotation-test.html")
    public ModelAndView anotherAnnotationTest()  {
        String message = "Hello Another Spring MVC";

        ModelAndView modelAndView = new ModelAndView("testView");
        modelAndView.addObject("message", message);

        return modelAndView;
    }
}
