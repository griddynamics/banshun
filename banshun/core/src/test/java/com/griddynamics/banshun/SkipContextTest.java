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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SkipContextTest {
    @Test
    public void skipContextTest() {
        ApplicationContext context;
        context = new ClassPathXmlApplicationContext("com/griddynamics/banshun/skipcontext/root-ctx.xml");
        StrictContextParentBean registry = (StrictContextParentBean) context.getBean("root");

        Set<String> ignoredLocations = registry.getIgnoredLocations();
        assertEquals(5, ignoredLocations.size());
        assertEquals(2, registry.getChildren().size());

        String result = registry.getResultConfigLocations().toString();

        assertTrue(result.contains("ctx2.xml") &&
                result.contains("ctx3.xml") &&
                result.contains("ctx4.xml") &&
                result.contains("ctx5.xml") &&
                result.contains("ctx6.xml"));
    }
}
