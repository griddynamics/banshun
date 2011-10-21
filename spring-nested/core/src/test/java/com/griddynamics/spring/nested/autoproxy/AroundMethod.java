package com.griddynamics.spring.nested.autoproxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class AroundMethod implements MethodInterceptor {
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        String result = (String) methodInvocation.proceed();
        return "AroundMethod: " + result;
    }
}
