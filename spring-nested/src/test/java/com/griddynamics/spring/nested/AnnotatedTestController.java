package com.griddynamics.spring.nested;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AnnotatedTestController {
    @RequestMapping("/annotation-test.html")
    public ModelAndView annotationTest()  {
        String message = "Hello Spring MVC";

        ModelAndView modelAndView = new ModelAndView("annotation-test");
        modelAndView.addObject("message", message);

        return modelAndView;
    }
}
