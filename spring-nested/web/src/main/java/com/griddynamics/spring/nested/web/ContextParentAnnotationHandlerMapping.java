package com.griddynamics.spring.nested.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;

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
 * @Description: implementation of {@link HandlersRegistry} made by copy-paste from Spring MVC 
 * usage: instantiate this bean in the root context, call registerXxx methods from nested children 
 * contexts to register controllers and request handlers.
 * don't forget to instantiate HandlerAdapters beans and follow Spring MVC conventions.
 * 
 **/
public class ContextParentAnnotationHandlerMapping extends DefaultAnnotationHandlerMapping implements HandlersRegistry {

	private final Map<Class, RequestMapping> cachedMappings = new HashMap<Class, RequestMapping>();
	
	/**
	 * @see org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping.determineUrlsForHandler(String)
	 * 
	 */
	protected String[] determineUrlsForHandlerByName(String beanName) {
		List urls = new ArrayList();
		if (beanName.startsWith("/")) {
			urls.add(beanName);
		}
		/* does nothing due to lack of 
		 * String[] aliases = getApplicationContext().getAliases(beanName);
		for (int i = 0; i < aliases.length; i++) {
			if (aliases[i].startsWith("/")) {
				urls.add(aliases[i]);
			}
		}*/
		return StringUtils.toStringArray(urls);
	}
	
	public Void registerByName(String url, Object controller) {
		String[] urls = determineUrlsForHandlerByName(url);
		register(urls, controller);
		return null;
	}

	private void register(String[] urls, Object handler) {
		if (!ObjectUtils.isEmpty(urls)) {
			// URL paths found: Let's consider it a handler.
			Assert.notNull(urls, "URL path array must not be null");
			for (int j = 0; j < urls.length; j++) {
				registerHandler(urls[j], handler);
			}
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("Rejected bean '" + handler + "': no URL paths identified");
			}
		}
	}

	public Void registerByAnnotation(Object handler) {
		register(determineUrlsByAnnotations(handler),handler);
		return null;
	}

	protected String[] determineUrlsByAnnotations(Object handler) {
		Class<? extends Object> handlerType = handler.getClass();
		RequestMapping mapping = AnnotationUtils.findAnnotation(handlerType, RequestMapping.class);

		if (mapping != null) {
			// @RequestMapping found at type level
			this.cachedMappings.put(handlerType, mapping);
			Set<String> urls = new LinkedHashSet<String>();
			String[] paths = mapping.value();
			if (paths.length > 0) {
				// @RequestMapping specifies paths at type level
				for (String path : paths ) {
					addUrlsForPath(urls, path);
				}
				return StringUtils.toStringArray(urls);
			}
			else {
				// actual paths specified by @RequestMapping at method level
				return determineUrlsForHandlerMethods(handlerType);
			}
		}
		else if (AnnotationUtils.findAnnotation(handlerType, Controller.class) != null) {
			// @RequestMapping to be introspected at method level
			return determineUrlsForHandlerMethods(handlerType);
		}
		else {
			return null;
		}
	}
	
	protected void validateHandler(Object handler, HttpServletRequest request) throws Exception {
		RequestMapping mapping = this.cachedMappings.get(handler.getClass());
		if (mapping == null) {
			mapping = AnnotationUtils.findAnnotation(handler.getClass(), RequestMapping.class);
		}
		if (mapping != null) {
			validateMapping(mapping, request);
		}
	}
}
