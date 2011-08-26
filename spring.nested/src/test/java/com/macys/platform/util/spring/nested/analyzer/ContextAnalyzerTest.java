package com.macys.platform.util.spring.nested.analyzer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.macys.platform.util.spring.nested.BeanReferenceInfo;
import com.macys.platform.util.spring.nested.ContextAnalyzer;
import com.macys.platform.util.spring.nested.Registry;

public class ContextAnalyzerTest {

    /**
     * This test checks whether the exceptions will happen during instantiation of cont
     */
    @Test
    public void testNested() {
        ApplicationContext context = new ClassPathXmlApplicationContext("com/macys/platform/util/spring/nested/analyzer/root-context.xml");
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