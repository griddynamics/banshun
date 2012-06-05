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
package com.griddynamics.banshun.web;

/**
 * @description: implementation is instantiated in the root context, and available
 * in nested children contexts for register Spring MVC handlers and controllers
 */
public interface HandlersRegistry {
    /**
     * registers handler for a given url. usually used for register bean implements Controller interface
     *
     * @param url        should start from slash
     * @param controller controller or handler for process request for the given url
     */
    Void registerByName(String name, Object handler);

    /**
     * used for register request handler mapped by {@link RequestMapping}
     */
    Void registerByAnnotation(Object handler);
}
