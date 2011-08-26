package com.macys.platform.util.spring.nested;

import java.lang.reflect.Proxy;

import junit.framework.TestCase;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RegistryBeanTest extends TestCase {

	protected void setUp() throws Exception {
	}
	public void testExact()	{
}
	public void tOstExact()	{
		check("com/macys/platform/util/spring/nested/exact-match-import.xml");
	}

	public void tOstWider()	{
		check("com/macys/platform/util/spring/nested/coarse-import.xml");
	}

	public void tOstTooConcreteImport()

	{
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("com/macys/platform/util/spring/nested/illegal-concrete-import.xml");
		Registry registry = (Registry)ctx.getBean("root", Registry.class);
		// checking laziness 
		assertTrue("have no exports due to laziness",hasNoExports(ctx));
		Object proxy = ctx.getBean("early-import");
		// force export
		ctx.getBean("export-declaration");
		// we have export here
		assertTrue("we have only parent interface export declaration", 
				registry.lookupByInterface(ExtendedChild.class).isEmpty() && 
				registry.lookupByInterface(NarrowDaddy.class).contains("just-bean"));
		
		try{// target is ready here, but types does not match
			proxy.toString();
		}catch(BeanNotOfRequiredTypeException e){
			assertEquals("just-bean",e.getBeanName());
			
			try{
				ctx.getBean("late-import").toString();
			}catch(BeanCreationException ee){
				assertEquals("late-import",ee.getBeanName());
				return;
			}
			fail("we should have BeanCreactionException here");
			
			return;
		}
		fail("we should have BeanNotOfRequiredTypeException here");
	}
	
	public void tOstWrongExport(){
		ApplicationContext ctx = new ClassPathXmlApplicationContext("com/macys/platform/util/spring/nested/wrong-export-class.xml");
		
		Object bean ;
		try{
				bean = ctx.getBean("late-import");
		}catch(Exception e){
			try{
				bean = ctx.getBean("early-import");
			}catch(Exception ee){
				return;
			}
			fail("we should have an Exception here.");
		}
		fail("we should have an Exception here");
		return;
	}
	
	protected void check(String configLocation) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(configLocation);
		
		// checking laziness 
		assertTrue("have no exports due to laziness",hasNoExports(ctx));
		
		Object proxy = ctx.getBean("early-import");
		try{
			proxy.toString();
			fail("attempt to invoke proxy without export should lead to exception");
		}catch(NoSuchBeanDefinitionException e)
		{	assertEquals("invoke bean without proper export","just-bean", e.getBeanName());			
		}
		// force export
		ctx.getBean("export-declaration");
		assertSame(proxy, ctx.getBean("early-import"));
		assertTrue("have export ref",hasExport(ctx, "just-bean"));
		
		assertEquals("proxies should refer the same bean instance",proxy.toString(), 
				ctx.getBean("late-import").toString());
		assertNotSame("proxy and bean can't be identical",proxy, 
				ctx.getBean("late-import"));
		assertSame("late import resolved to a bean instance itself",ctx.getBean("late-import"), 
				ctx.getBean("just-bean"));
		assertTrue("early import gives us a proxy",proxy instanceof Proxy);
		assertFalse("late import Does NoT give us a proxy",ctx.getBean("late-import") instanceof Proxy);
	}

	private boolean hasExport(ClassPathXmlApplicationContext ctx, String bean) {
		Registry registry = (Registry)ctx.getBean("root", Registry.class);
		return registry.lookupByInterface(ExtendedChild.class).contains(bean)&&registry.lookupByInterface(NarrowDaddy.class).contains(bean);
	}

	private boolean hasNoExports(ApplicationContext ctx) {
		Registry registry = (Registry)ctx.getBean("root", Registry.class);
		return registry.lookupByInterface(ExtendedChild.class).isEmpty() && 
				registry.lookupByInterface(NarrowDaddy.class).isEmpty();
	}
}
