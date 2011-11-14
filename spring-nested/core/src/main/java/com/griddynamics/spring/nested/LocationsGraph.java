package com.griddynamics.spring.nested;

import java.util.*;

/**
 * Copyright (c) 2011 Grid Dynamics Consulting Services, Inc, All Rights
 * Reserved http://www.griddynamics.com
 * <p/>
 * For information about the licensing and copyright of this document please
 * contact Grid Dynamics at info@griddynamics.com.
 * <p/>
 * $Id: $
 *
 * @Project: Spring Nested
 * @Description:
 */
public class LocationsGraph {
    private Map<String, HashSet<String>> locationsDependOn = new HashMap<String, HashSet<String>>();
    private Map<String, HashSet<String>> locationsDependenciesOf = new HashMap<String, HashSet<String>>();

    public LocationsGraph(Map<String, List<BeanReferenceInfo>> imports, Map<String, BeanReferenceInfo> exports) {
        for (String beanName : imports.keySet()) {
            String expLoc = exports.get(beanName).getLocation();
            if (!locationsDependenciesOf.containsKey(expLoc)) {
                locationsDependenciesOf.put(expLoc, new HashSet<String>());
            }
            for (BeanReferenceInfo refInfo : imports.get(beanName)) {
                if (!locationsDependOn.containsKey(refInfo.getLocation())) {
                    locationsDependOn.put(refInfo.getLocation(), new HashSet<String>());
                }
                locationsDependOn.get(refInfo.getLocation()).add(expLoc);
                locationsDependenciesOf.get(expLoc).add(refInfo.getLocation());
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
        Map<String, HashSet<String>> locationDependencies = isDependsOnMode ? locationsDependOn : locationsDependenciesOf;
        if (locationDependencies.containsKey(loc)) {
            for (String dependsOn : locationDependencies.get(loc)) {
                if (!marked.contains(dependsOn)) {
                    transitiveClosure(dependsOn, marked, isDependsOnMode);
                }
            }
        }
    }
}
