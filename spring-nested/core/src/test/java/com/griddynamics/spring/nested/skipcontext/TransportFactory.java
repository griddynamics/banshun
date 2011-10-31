package com.griddynamics.spring.nested.skipcontext;

import com.griddynamics.spring.nested.analyzer.SuperInterface;

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

public class TransportFactory {
    public static SuperInterface getObject() throws Exception {
        throw new Exception("exception!");
    }
}
