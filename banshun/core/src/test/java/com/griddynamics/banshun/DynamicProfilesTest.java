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

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DynamicProfilesTest {
    @Test
    public void dynamicProfileTest0() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/banshun/dynamic/root-ctx.xml");
        StrictContextParentBean registry = (StrictContextParentBean) context.getBean("root");
        registry.setRunOnlyServices(new String[] {"object1"});
        registry.afterPropertiesSet();

        assertEquals(1, registry.getResultConfigLocations().size());

        final Pattern pattern = Pattern.compile("\\[.*ctx1\\.xml\\]");
        String result = registry.getResultConfigLocations().toString();

        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void dynamicProfileTest1() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/banshun/dynamic/root-ctx.xml");
        StrictContextParentBean registry = (StrictContextParentBean) context.getBean("root");
        registry.setRunOnlyServices(new String[] {"context3", "context7"});
        registry.afterPropertiesSet();

        assertEquals(4, registry.getResultConfigLocations().size());

        final Pattern pattern = Pattern.compile("\\[.*ctx1\\.xml(.*ctx7\\.xml.*ctx2\\.xml|.*ctx2\\.xml.*ctx7\\.xml).*ctx3\\.xml.*\\]");
        String result = registry.getResultConfigLocations().toString();

        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void dynamicProfileTest2() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/banshun/dynamic/root-ctx.xml");
        StrictContextParentBean registry = (StrictContextParentBean) context.getBean("root");
        registry.setRunOnlyServices(new String[]{"context6"});
        registry.afterPropertiesSet();

        assertEquals(4, registry.getResultConfigLocations().size());

        final Pattern pattern = Pattern.compile("\\[.*ctx1\\.xml(.*ctx7\\.xml.*ctx2\\.xml|.*ctx2\\.xml.*ctx7\\.xml).*ctx6\\.xml.*\\]");
        String result = registry.getResultConfigLocations().toString();

        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void dynamicProfileTest3() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/banshun/dynamic/root-ctx.xml");
        StrictContextParentBean registry = (StrictContextParentBean) context.getBean("root");
        registry.setRunOnlyServices(new String[] {"context5"});
        registry.afterPropertiesSet();

        assertEquals(7, registry.getResultConfigLocations().size());

        final Pattern mainPattern = Pattern.compile("\\[.*ctx1\\.xml(.*ctx7\\.xml.*ctx2\\.xml|.*ctx2\\.xml.*ctx7\\.xml)(.*ctx6\\.xml.*ctx3\\.xml|.*ctx3\\.xml.*ctx6\\.xml).*\\]");
        final Pattern pattern5After4And6 = Pattern.compile("\\[(.*ctx4\\.xml.*ctx6\\.xml|.*ctx6\\.xml.*ctx4\\.xml).*ctx5\\.xml.*\\]");
        final Pattern pattern4After3 = Pattern.compile("\\[.*ctx3\\.xml.*ctx4\\.xml.*\\]");
        String result = registry.getResultConfigLocations().toString();

        assertTrue(mainPattern.matcher(result).matches());
        assertTrue(pattern4After3.matcher(result).matches());
        assertTrue(pattern5After4And6.matcher(result).matches());
    }
}
