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

import java.util.*;

public class LocationsGraph {

    private Map<String, HashSet<String>> dependOn = new HashMap<String, HashSet<String>>();
    private Map<String, HashSet<String>> dependenciesOf = new HashMap<String, HashSet<String>>();

    public LocationsGraph(Map<String, List<BeanReferenceInfo>> imports, Map<String, BeanReferenceInfo> exports) {
        for (String beanName : imports.keySet()) {
            String expLoc = exports.get(beanName).getLocation();

            if (!dependenciesOf.containsKey(expLoc)) {
                dependenciesOf.put(expLoc, new HashSet<String>());
            }
            for (BeanReferenceInfo refInfo : imports.get(beanName)) {
                if (!dependOn.containsKey(refInfo.getLocation())) {
                    dependOn.put(refInfo.getLocation(), new HashSet<String>());
                }
                dependOn.get(refInfo.getLocation()).add(expLoc);
                dependenciesOf.get(expLoc).add(refInfo.getLocation());
            }
        }
    }

    public List<String> filterConfigLocations(List<String> limitedLocations, String[] allLocations) {
        Set<String> marked = new HashSet<String>();
        List<String> resultLocationList = new ArrayList<String>(Arrays.asList(allLocations));

        if (!limitedLocations.isEmpty()) {
            for (String loc : limitedLocations) {
                transitiveClosure(loc, marked, true);
            }
            resultLocationList.retainAll(marked);
        }
        return resultLocationList;
    }

    public void transitiveClosure(String loc, Set<String> marked, boolean isDependsOnMode) {
        marked.add(loc);
        Map<String, HashSet<String>> locationDependencies = isDependsOnMode ? dependOn : dependenciesOf;

        if (locationDependencies.containsKey(loc)) {
            for (String dependsOn : locationDependencies.get(loc)) {
                if (!marked.contains(dependsOn)) {
                    transitiveClosure(dependsOn, marked, isDependsOnMode);
                }
            }
        }
    }
}
