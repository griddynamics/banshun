package com.griddynamics.spring.nested;

import org.junit.Test;
import static org.junit.Assert.*;

import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

public class NestedControllerTest {
    @Test
    public void test() throws Exception {
        ServletTester tester = new ServletTester();

        tester.addServlet(TestServlet.class, "*.html");
        tester.start();

        HttpTester request = new HttpTester();
        request.setMethod("GET");
        request.setHeader("Host","tester");
        request.setURI("/nested-controller-test.html");
        request.setVersion("HTTP/1.0");

        HttpTester response = new HttpTester();
        response.parse(tester.getResponses(request.generate()));

        assertEquals("{message=Hello Spring MVC}", response.getContent());
    }
}
