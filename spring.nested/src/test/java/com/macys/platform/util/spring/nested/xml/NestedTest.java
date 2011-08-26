package com.macys.platform.util.spring.nested.xml;

import com.macys.platform.util.spring.nested.Registry;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 14, 2010
 * Time: 9:23:53 AM
 */
public class NestedTest {

    private static Registry registry;

    @BeforeClass
    public static void before() {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/macys/platform/util/spring/nested/xml/root-context.xml");
        registry = (Registry) context.getBean("root");
    }

    @Test
    public void testNested() {
        Child first = registry.lookup("firstChild", Child.class);
        Child second = registry.lookup("secondChild", Child.class);

        assertNotNull(first);
        assertEquals("firstChild", first.getName() );

        assertNotNull(second);
        assertEquals("secondChild", second.getName() );
    }

    @Test
    public void testChildWithChild() {
        Parent parent = registry.lookup("parent", Parent.class);

        assertNotNull(parent);
        assertNotNull(parent.getChild());
        assertEquals("secondChild", parent.getChild().getName() );
    }
}
