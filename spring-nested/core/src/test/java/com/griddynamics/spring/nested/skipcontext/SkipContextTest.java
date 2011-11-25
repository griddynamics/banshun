package com.griddynamics.spring.nested.skipcontext;

import com.griddynamics.spring.nested.StrictContextParentBean;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Set;

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

public class SkipContextTest {
    @Test
    public void skipContextTest() {
        ApplicationContext context;
        context = new ClassPathXmlApplicationContext("com/griddynamics/spring/nested/skipcontext/root-ctx.xml");
        StrictContextParentBean registry = (StrictContextParentBean) context.getBean("root");

        Set<String> ignoredLocations = registry.getIgnoredLocations();
        assertEquals(5, ignoredLocations.size());
        assertEquals(2, registry.getChildren().size());

        assertTrue(containsCtx(ignoredLocations, "ctx2.xml"));
        assertTrue(containsCtx(ignoredLocations, "ctx3.xml"));
        assertTrue(containsCtx(ignoredLocations, "ctx4.xml"));
        assertTrue(containsCtx(ignoredLocations, "ctx5.xml"));
        assertTrue(containsCtx(ignoredLocations, "ctx6.xml"));
    }

    private boolean containsCtx(Set<String> set, String query) {
        for (String s : set) {
            if (s.contains(query)) {
                return true;
            }
        }
        return false;
    }
}
