package com.griddynamics.banshun.xml;

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
 * User: oleg
 * Date: Sep 14, 2010
 * Time: 6:27:19 PM
 */
public interface Parent {

    Child getChild();

    void setChild(Child child);
}
