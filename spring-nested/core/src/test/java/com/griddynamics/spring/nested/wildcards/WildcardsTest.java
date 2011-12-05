package com.griddynamics.spring.nested.wildcards;

import com.griddynamics.spring.nested.ContextParentBean;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

public class WildcardsTest {
    private ContextParentBean registry = null;

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/spring/nested/wildcards/root-ctx.xml");
        registry = (ContextParentBean) context.getBean("root");
    }

    @Test
    public void wildcardsTest0() throws Exception {
        registry.setConfigLocations(new String[] {
                "/com/griddynamics/spring/nested/wildcards/*7.xml"
            }
        );
        registry.afterPropertiesSet();

        assertEquals(1, registry.getResultConfigLocations().size());

        final Pattern pattern = Pattern.compile("\\[.*ctx7\\.xml\\]");
        String result = registry.getResultConfigLocations().toString();

        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void wildcardsTest1() throws Exception {
        registry.setConfigLocations(new String[] {
                "/com/griddynamics/spring/nested/wildcards/sub1/ctx1.xml",
                "/com/griddynamics/spring/nested/wildcards/sub2/*.xml",
                "/com/griddynamics/spring/nested/wildcards/sub1/*.xml",
                "/com/griddynamics/spring/nested/wildcards/sub2/ctx6.xml",
                "/com/griddynamics/spring/nested/wildcards/ctx*.xml"
            }
        );
        registry.afterPropertiesSet();

        assertEquals(8, registry.getResultConfigLocations().size());

        final Pattern pattern1 = Pattern.compile("\\[.*ctx1\\.xml(.*ctx2\\.xml.*ctx4\\.xml|.*ctx4\\.xml.*ctx2\\.xml)(.*ctx3\\.xml.*ctx5\\.xml|.*ctx5\\.xml.*ctx3\\.xml).*ctx6\\.xml.*\\]");
        final Pattern pattern2 = Pattern.compile(".*ctx7\\.xml.*ctx8\\.xml.*|.*ctx8\\.xml.*ctx7\\.xml.*");
        String result = registry.getResultConfigLocations().toString();

        assertTrue(pattern1.matcher(result).matches());
        assertTrue(pattern2.matcher(result).matches());
    }

    @Test
    public void wildcardsTest2() throws Exception {
        registry.setConfigLocations(new String[] {
                "classpath:/com/griddynamics/spring/nested/wildcards/sub1/ctx1.xml",
                "/com/griddynamics/spring/nested/wildcards/sub1/ctx1.xml",
                "classpath:/com/griddynamics/spring/nested/wildcards/sub1/ctx1.xml",
                "classpath:/com/griddynamics/spring/nested/wildcards/sub1/ctx1*.xml"
            }
        );
        registry.afterPropertiesSet();

        assertEquals(1, registry.getResultConfigLocations().size());

        final Pattern pattern = Pattern.compile("\\[.*ctx1\\.xml\\]");
        String result = registry.getResultConfigLocations().toString();

        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void excludeLocationsTest() throws Exception {
        registry.setExcludeConfigLocations(new String[] {"/com/griddynamics/spring/nested/wildcards/ctx*.xml"});
        registry.setConfigLocations(new String[] {
                "/com/griddynamics/spring/nested/wildcards/sub1/*.xml",
                "/com/griddynamics/spring/nested/wildcards/sub2/*.xml",
                "/com/griddynamics/spring/nested/wildcards/ctx*.xml"
            }
        );
        registry.afterPropertiesSet();

        assertEquals(6, registry.getResultConfigLocations().size());

        String result = registry.getResultConfigLocations().toString();

        assertTrue(result.contains("ctx1.xml") &&
                result.contains("ctx3.xml") &&
                result.contains("ctx5.xml") &&
                result.contains("ctx2.xml") &&
                result.contains("ctx4.xml") &&
                result.contains("ctx6.xml"));
    }
}
