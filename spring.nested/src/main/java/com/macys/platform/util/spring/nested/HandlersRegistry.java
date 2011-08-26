package com.macys.platform.util.spring.nested;

/** implementation is instantiated in the root context, and available 
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
