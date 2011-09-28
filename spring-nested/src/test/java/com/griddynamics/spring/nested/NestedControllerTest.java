package com.griddynamics.spring.nested;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;

public class NestedControllerTest {
    private ServletTester tester;
    private HttpTester request;
    private HttpTester response;

    @Before
    public void init() throws Exception {
        tester = new ServletTester();
        tester.addServlet(TestServlet.class, "*.html");
        tester.start();
    }

    @Test
    public void nestedControllerTest() throws Exception {
        doTest("/nested-controller-test.html");

        assertEquals("{message=Hello Spring MVC}", response.getContent());
    }

    @Test
    public void annotatedControllerTest() throws Exception {
        doTest("/annotation-test.html");

        assertEquals("{message=Hello Spring MVC}", response.getContent());
    }

    @Test
    public void handlerTest() throws Exception {
        doTest("/handler-test.html");

        assertEquals("Hello Spring MVC", response.getContent());
    }

    private void updateRequest(String URI) {
        request = new HttpTester();
        request.setMethod("GET");
        request.setHeader("Host","tester");
        request.setVersion("HTTP/1.0");
        request.setURI(URI);
    }

    private void doTest(String URI) throws Exception {
        updateRequest(URI);
        response = new HttpTester();
        response.parse(tester.getResponses(request.generate()));
    }
}
