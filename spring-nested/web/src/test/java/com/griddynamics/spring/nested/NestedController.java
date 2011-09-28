package com.griddynamics.spring.nested;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NestedController implements Controller {
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message = "Hello Spring MVC";

        ModelAndView modelAndView = new ModelAndView("testView");
        modelAndView.addObject("message", message);

        return modelAndView;
    }
}
