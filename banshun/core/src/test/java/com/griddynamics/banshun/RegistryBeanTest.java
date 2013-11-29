/**
 *    Copyright 2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 *    http://www.griddynamics.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *  @Project: Banshun
 * */
package com.griddynamics.banshun;

import org.junit.Test;
import org.springframework.aop.TargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

public class RegistryBeanTest {

    @Test
    public void importBeanWithSameClass() {
        importValidBeanTest("com/griddynamics/banshun/registry/exact-match-import.xml");
    }

    @Test
    public void importBeanWithSuperclass() {
        importValidBeanTest("com/griddynamics/banshun/registry/coarse-import.xml");
    }

    @Test
    public void importBeanWithSubclass() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("com/griddynamics/banshun/registry/illegal-concrete-import.xml");

        assertTrue("have no exports due to laziness", hasNoExports(ctx));
        Object proxy = ctx.getBean("early-import");

        // force export
        ctx.getBean("export-declaration");

        try {
            // target is ready here, but types does not match
            proxy.toString();
        } catch (BeanNotOfRequiredTypeException e) {
            assertEquals("just-bean", e.getBeanName());

            try {
                Object b = ctx.getBean("late-import").toString();
                b.toString();
            } catch (BeansException e2) {
                assertEquals("just-bean", ((BeanNotOfRequiredTypeException) e2).getBeanName());
                return;
            }

            fail("we should have BeanCreactionException here");
            return;
        }
        fail("we should have BeanNotOfRequiredTypeException here");
    }

    @Test
    public void misconfiguredExport() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("com/griddynamics/banshun/registry/wrong-export-class.xml");

        Object bean;
        try {
            bean = ctx.getBean("late-import");
            bean.toString();
        } catch (Exception e) {
            try {
                bean = ctx.getBean("early-import");
                bean.toString();
            } catch (Exception e2) {
                return;
            }
            fail("we should have an Exception here.");
        }
        fail("we should have an Exception here");
    }

    protected void importValidBeanTest(String configLocation) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(configLocation);

        assertTrue("have no exports due to laziness", hasNoExports(ctx));

        Object proxy = ctx.getBean("early-import");
        try {
            proxy.toString();
            fail("attempt to invoke proxy without export should lead to exception");
        } catch (NoSuchBeanDefinitionException e) {
            assertEquals("invoke bean without proper export", "just-bean", e.getBeanName());
        }

        // force export
        ctx.getBean("export-declaration");

        assertSame(proxy, ctx.getBean("early-import"));
        assertFalse("have export ref", hasExport(ctx, "just-bean"));

        assertEquals("proxies should refer the same bean instance", proxy.toString(), ctx.getBean("late-import").toString());
        assertSame("proxies should be the same instance", proxy, ctx.getBean("late-import"));
        assertTrue("early import gives us a proxy", proxy instanceof Proxy);
        assertTrue("late import gives us a proxy", ctx.getBean("late-import") instanceof Proxy);
    }

    private boolean hasExport(ClassPathXmlApplicationContext ctx, String bean) {
        String beanName = bean + ContextParentBean.TARGET_SOURCE_SUFFIX;
        if (ctx.containsBean(beanName)) {
            ExportTargetSource elits = (ExportTargetSource) ctx.getBean(beanName, TargetSource.class);
            return elits.getBeanFactory() == null;
        }
        return false;
    }

    private boolean hasNoExports(ApplicationContext ctx) {
        String[] beanNames = ctx.getBeanNamesForType(TargetSource.class);
        for (String beanName : beanNames) {
            if (beanName.contains(ContextParentBean.TARGET_SOURCE_SUFFIX)) {
                ExportTargetSource elits = (ExportTargetSource) ctx.getBean(beanName, TargetSource.class);
                if (elits.getBeanFactory() != null) {
                    return false;
                }
            }
        }
        return true;
    }
}
