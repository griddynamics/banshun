package com.griddynamics.spring.nested;

import java.util.Collection;

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
 * @Description: Registry for export and import services by name with a constraint by 
 * an interface.   
 * The singleton bean implements this interface instantiated by the root context and available 
 * for the nested children context via intrinsic Spring feature "parent context". 
 */
public interface Registry {
	/** 
	 * export the given service reference
	 * */
	Void export(ExportRef ref);
	/**
	 * imports a service by name
	 * @param name key for find a service. usually camel case name used during export
	 * @param clazz expected interface for the service. should be match with the interface used during an export
	 * @return proxy for the requested service
	 * */
	<T> T lookup(final String name, final Class<T> clazz);
}