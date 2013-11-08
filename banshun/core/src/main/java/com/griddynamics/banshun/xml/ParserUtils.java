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
package com.griddynamics.banshun.xml;

import org.springframework.beans.factory.CannotLoadBeanClassException;


public final class ParserUtils {

    public static final String DEFAULT_ROOT_FACTORY_NAME = "root";

    // XML attribute names
    public static final String
            ID_ATTR = "id",
            INTERFACE_ATTR = "interface",
            NAME_ATTR = "name",
            REF_ATTR = "ref",
            ROOT_ATTR = "root";


    public static Class<?> findClass(String className, String beanName, String resourceDescription) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new CannotLoadBeanClassException(resourceDescription, beanName, className, ex);
        }
    }

    public static boolean isBlank(String str) {
        return str == null || str.length() == 0;
    }


    private ParserUtils() {}
}
