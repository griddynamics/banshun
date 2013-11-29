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

import com.griddynamics.banshun.fixtures.MiddleFace;
import com.griddynamics.banshun.fixtures.RootFace;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;


public class ContextAnalyzerTest {

    @Test
    public void lookupExportedBeans() {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/banshun/analyzer/root-context.xml");
        Registry registry = context.getBean("root", Registry.class);
        
        MiddleFace first = registry.lookup("firstObject", MiddleFace.class);
        RootFace second = registry.lookup("secondObject", RootFace.class);

        assertNotNull(first);
        assertNotNull(second);
    }
    
    @Test
    public void verifyImportTypes() {
        ContextAnalyzer analyzer = new ContextAnalyzer();
        String beanName = "firstObject";
        
        BeanReferenceInfo exportRef = new BeanReferenceInfo();
        exportRef.setBeanInterface(RootFace.class);
        exportRef.setBeanName(beanName);
        
        analyzer.putInExports(exportRef);
        
        BeanReferenceInfo importRef1 = new BeanReferenceInfo();
        importRef1.setBeanInterface(RootFace.class);
        importRef1.setBeanName(beanName);
        
        analyzer.putInImports(importRef1);
        
        assertTrue(analyzer.areImportsTypesCorrect());
        
        BeanReferenceInfo importRef2 = new BeanReferenceInfo();
        importRef2.setBeanInterface(MiddleFace.class);
        importRef2.setBeanName(beanName);
        
        analyzer.putInImports(importRef2);
        
        assertFalse(analyzer.areImportsTypesCorrect());
    }
    
    @Test
    public void areThereImportsWithoutExports() {
        ContextAnalyzer analyzer = new ContextAnalyzer();
        String beanName = "firstObject";
        
        BeanReferenceInfo importRef1 = new BeanReferenceInfo();
        importRef1.setBeanName(beanName);
        
        analyzer.putInImports(importRef1);
        
        assertTrue(analyzer.areThereImportsWithoutExports());
        
        BeanReferenceInfo exportRef = new BeanReferenceInfo();
        exportRef.setBeanName(beanName);
        
        analyzer.putInExports(exportRef);
        
        assertFalse(analyzer.areThereImportsWithoutExports());
    }
    
    @Test
    public void areThereExportsWithoutImport() {
        ContextAnalyzer analyzer = new ContextAnalyzer();
        String beanName = "firstObject";
        
        BeanReferenceInfo exportRef = new BeanReferenceInfo();
        exportRef.setBeanName(beanName);
        
        analyzer.putInExports(exportRef);
        
        assertFalse(analyzer.areThereExportsWithoutImport());
        
        BeanReferenceInfo importRef1 = new BeanReferenceInfo();
        importRef1.setBeanName(beanName);
        
        analyzer.putInImports(importRef1);
        
        assertTrue(analyzer.areThereExportsWithoutImport());
    }
}
