package com.griddynamics.spring.nested;

import java.lang.reflect.Proxy;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.springframework.aop.TargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Copyright (c) 2011 Grid Dynamics Consulting Services, Inc, All Rights
 * Reserved http://www.griddynamics.com
 * <p/>
 * For information about the licensing and copyright of this document please
 * contact Grid Dynamics at info@griddynamics.com.
 * <p/>
 * $Id: $
 *
 * @Project: Spring Nested
 * @Description:
 */
public class RegistryBeanTest extends TestCase {
    public void testExact() {
        check("com/griddynamics/spring/nested/exact-match-import.xml");
    }

    public void testWider() {
        check("com/griddynamics/spring/nested/coarse-import.xml");
    }

    public void testTooConcreteImport() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("com/griddynamics/spring/nested/illegal-concrete-import.xml");

        Assert.assertTrue("have no exports due to laziness", hasNoExports(ctx));
        Object proxy = ctx.getBean("early-import");

        // force export
        ctx.getBean("export-declaration");

        try {
            // target is ready here, but types does not match
            proxy.toString();
        } catch (BeanNotOfRequiredTypeException e) {
            Assert.assertEquals("just-bean", e.getBeanName());

            try {
                Object b = ctx.getBean("late-import");
                b.toString();
            } catch (BeansException ee) {
                Assert.assertEquals("just-bean", ((BeanNotOfRequiredTypeException) ee).getBeanName());
                return;
            }

            Assert.fail("we should have BeanCreactionException here");
            return;
        }
        Assert.fail("we should have BeanNotOfRequiredTypeException here");
    }

    public void testWrongExport() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("com/griddynamics/spring/nested/wrong-export-class.xml");

        Object bean;
        try {
            bean = ctx.getBean("late-import");
            bean.toString();
        } catch (Exception e) {
            try {
                bean = ctx.getBean("early-import");
                bean.toString();
            } catch (Exception ee) {
                return;
            }
            Assert.fail("we should have an Exception here.");
        }
        Assert.fail("we should have an Exception here");
    }

    protected void check(String configLocation) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(configLocation);

        Assert.assertTrue("have no exports due to laziness", hasNoExports(ctx));

        Object proxy = ctx.getBean("early-import");
        try {
            proxy.toString();
            Assert.fail("attempt to invoke proxy without export should lead to exception");
        } catch (NoSuchBeanDefinitionException e) {
            Assert.assertEquals("invoke bean without proper export", "just-bean", e.getBeanName());
        }

        // force export
        ctx.getBean("export-declaration");

        Assert.assertSame(proxy, ctx.getBean("early-import"));
        Assert.assertFalse("have export ref", hasExport(ctx, "just-bean"));

        Assert.assertEquals("proxies should refer the same bean instance", proxy.toString(), ctx.getBean("late-import").toString());
        Assert.assertSame("proxies should refer the same bean instance", proxy, ctx.getBean("late-import"));
        Assert.assertTrue("early import gives us a proxy", proxy instanceof Proxy);
        Assert.assertTrue("late import gives us a proxy", ctx.getBean("late-import") instanceof Proxy);
    }

    private boolean hasExport(ClassPathXmlApplicationContext ctx, String bean) {
        String beanName = bean + ContextParentBean.TARGET_SOURCE_SUFFIX;
        if (ctx.containsBean(beanName)) {
            ExportLazyInitTargetSource elits = (ExportLazyInitTargetSource) ctx.getBean(beanName, TargetSource.class);
            return elits.getBeanFactory() == null;
        }
        return false;
    }

    private boolean hasNoExports(ApplicationContext ctx) {
        String[] beanNames = ctx.getBeanNamesForType(TargetSource.class);
        for (String beanName : beanNames) {
            if (beanName.contains(ContextParentBean.TARGET_SOURCE_SUFFIX)) {
                ExportLazyInitTargetSource elits = (ExportLazyInitTargetSource) ctx.getBean(beanName, TargetSource.class);
                if (elits.getBeanFactory() != null) {
                    return false;
                }
            }
        }
        return true;
    }
}
