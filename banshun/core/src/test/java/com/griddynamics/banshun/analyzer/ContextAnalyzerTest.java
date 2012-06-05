package com.griddynamics.banshun.analyzer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.griddynamics.banshun.BeanReferenceInfo;
import com.griddynamics.banshun.ContextAnalyzer;
import com.griddynamics.banshun.Registry;

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
public class ContextAnalyzerTest {

    /**
     * This test checks whether the exceptions will happen during instantiation of cont
     */
    @Test
    public void testNested() {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/griddynamics/banshun/analyzer/root-context.xml");
        Registry registry = (Registry) context.getBean("root");
        
        SuperInterface first = registry.lookup("firstObject", SuperInterface.class);
        SubInterface second = registry.lookup("secondObject", SubInterface.class);

        assertNotNull(first);
        assertNotNull(second);
    }
    
    @Test
    public void testAreImportsTypesCorrect() {
        ContextAnalyzer analyzer = new ContextAnalyzer();
        String beanName = "firstObject";
        
        BeanReferenceInfo exportRef = new BeanReferenceInfo();
        exportRef.setBeanInterface(SubInterface.class);
        exportRef.setBeanName(beanName);
        
        analyzer.putInExports(exportRef);
        
        BeanReferenceInfo importRef1 = new BeanReferenceInfo();
        importRef1.setBeanInterface(SubInterface.class);
        importRef1.setBeanName(beanName);
        
        analyzer.putInImports(importRef1);
        
        assertTrue(analyzer.areImportsTypesCorrect());
        
        BeanReferenceInfo importRef2 = new BeanReferenceInfo();
        importRef2.setBeanInterface(SuperInterface.class);
        importRef2.setBeanName(beanName);
        
        analyzer.putInImports(importRef2);
        
        assertFalse(analyzer.areImportsTypesCorrect());
    }
    
    @Test
    public void testAreThereImportsWithoutExports() {
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
    public void testAreThereExportsWithoutImports() {
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