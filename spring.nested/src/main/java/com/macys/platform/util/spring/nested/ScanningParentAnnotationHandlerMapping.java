package com.macys.platform.util.spring.nested;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
public class ScanningParentAnnotationHandlerMapping extends ContextParentAnnotationHandlerMapping implements InitializingBean{

	public boolean isUrlAnnotatedBean(Object object){
		return determineUrlsByAnnotations(object) != null;
	}
	
	public Collection<Object> parseContextForHandlers(ApplicationContext context){
		Map<String,Object> controllerBeansMap = context.getBeansWithAnnotation(Controller.class);
		Map<String,Object> requestBeansMap = context.getBeansWithAnnotation(RequestMapping.class); 
		for (String name : requestBeansMap.keySet()) {
			if (!controllerBeansMap.keySet().contains(name)){
				controllerBeansMap.put(name, requestBeansMap.get(name));
			}
		}
		for (Entry<String, Object> beanEntry : controllerBeansMap.entrySet()) {
			if(isUrlAnnotatedBean(beanEntry.getValue())){
				registerByAnnotation(beanEntry.getValue());
			}
		}
		return controllerBeansMap.values();
		
	}

    public void afterPropertiesSet() throws Exception {
        parseContextForHandlers(this.getApplicationContext());
    }


}
