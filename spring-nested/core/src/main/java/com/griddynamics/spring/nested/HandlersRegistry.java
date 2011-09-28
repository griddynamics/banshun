package com.griddynamics.spring.nested;

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
 * @Description: implementation is instantiated in the root context, and available 
 * in nested children contexts for register Spring MVC handlers and controllers  */
public interface HandlersRegistry {
	/** registers handler for a given url. usually used for register bean implements Controller interface 
	 * @param url should start from slash
	 * @param controller controller or handler for process request for the given url  
	 *   */
	Void registerByName(String name, Object handler);
	/**
	 * used for register request handler mapped by {@link RequestMapping}
	 */
	Void registerByAnnotation(Object handler);
}
