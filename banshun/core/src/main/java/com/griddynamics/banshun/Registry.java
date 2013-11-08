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
package com.griddynamics.banshun;

/**
 * Registry for exported and imported services by name with a constraint by
 * an interface.
 *
 * The singleton bean implements this interface instantiated by the root context
 * and available for the nested children context via intrinsic Spring feature
 * "parent context".
 */
public interface Registry {

    /**
     * Exports the given service reference.
     */
    Void export(ExportRef ref);

    /**
     * Imports a service by the name.
     *
     * @param name  The key to find a service with. It's usually camelCase name used during export.
     * @param clazz The expected interface for the service. It should match with the interface used during an export.
     * @return A proxy of the requested service.
     */
    <T> T lookup(final String name, final Class<T> clazz);
}
