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

import junit.framework.Assert;
import org.junit.Test;

import java.util.*;

public class DependencySorterTest {

    List<DependencySorter.Location> locations;

    @Test
    public void simpleTestsSort() {
        fillSimpleData();
        DependencySorter sorter = new DependencySorter(getLocations(), getImports(), getExports());
        String[] locations = sorter.sort();
        checkLocations(locations, getLocations());
    }

    @Test
    public void sortWithRedundantExports() {
        fillLocationsWithRedundantExports();
        DependencySorter sorter = new DependencySorter(getLocations(), getImports(), getExports());
        String[] locations = sorter.sort();
        checkLocations(locations, getLocations());
    }

    @Test
    public void sortWithSolvableConflict() {
        fillLocationsWithSolvableConlict();
        DependencySorter sorter = new DependencySorter(getLocations(), getImports(), getExports());
        String[] locations = sorter.sort();
        checkLocations(locations, new String[]{"some1.test.location.xml", "some3.test.location.xml", "some2.test.location.xml", "some4.test.location.xml"});
    }

    @Test
    public void sortWithConflicts() {
        fillLocationsWithConlicts();
        DependencySorter sorter = new DependencySorter(getLocations(), getImports(), getExports());
        sorter.setProhibitCycles(false);
        String[] locations = sorter.sort();
        checkLocations(locations, new String[]{"some2.test.location.xml", "some1.test.location.xml", "some3.test.location.xml", "some4.test.location.xml", "some5.test.location.xml"});
        checkLocations(sorter.getConflictContextGroup(), new String[]{"some3.test.location.xml", "some4.test.location.xml"});
    }

    private void checkLocations(String[] locations1, String[] locations2) {
        Assert.assertEquals(locations1.length, locations2.length);
        for (int i = 0; i < locations1.length; i++) {
            Assert.assertEquals(locations1[i], locations2[i]);
        }
    }

    private Map<String, BeanReferenceInfo> getExports() {
        Map<String, BeanReferenceInfo> exports = new HashMap<String, BeanReferenceInfo>();
        for (DependencySorter.Location location : locations) {
            if (!location.getExportBeans().isEmpty()) {
                for (BeanReferenceInfo exportBean : location.getExportBeans()) {
                    exports.put(exportBean.getBeanName(), exportBean);
                }
            }
        }
        return exports;
    }

    private Map<String, List<BeanReferenceInfo>> getImports() {
        Map<String, List<BeanReferenceInfo>> imports = new HashMap<String, List<BeanReferenceInfo>>();
        for (DependencySorter.Location location : locations) {
            if (!location.getImportBeans().isEmpty()) {
                for (BeanReferenceInfo importBean : location.getImportBeans()) {
                    if (!imports.containsKey(importBean.getBeanName())) {
                        imports.put(importBean.getBeanName(), new LinkedList<BeanReferenceInfo>());
                    }
                    imports.get(importBean.getBeanName()).add(importBean);
                }
            }
        }
        return imports;
    }

    private String[] getLocations() {
        String[] locationsArray = new String[locations.size()];
        int i = 0;
        for (DependencySorter.Location location : locations) {
            locationsArray[i++] = location.getLocationName();
        }
        return locationsArray;
    }

    private void fillSimpleData() {
        locations = new LinkedList<DependencySorter.Location>();
        //1
        String locationName = "some1.test.location.xml";
        Set<BeanReferenceInfo> imports = new HashSet<BeanReferenceInfo>();
        Set<BeanReferenceInfo> exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("TestBean1", Integer.class, locationName));
        DependencySorter.Location location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
        //2
        locationName = "some2.test.location.xml";
        imports = new HashSet<BeanReferenceInfo>();
        imports.add(createBean("TestBean1", Object.class, locationName));
        exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("TestBean2", Integer.class, locationName));
        location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
        //3
        locationName = "some3.test.location.xml";
        imports = new HashSet<BeanReferenceInfo>();
        imports.add(createBean("TestBean1", Object.class, locationName));
        imports.add(createBean("TestBean2", Object.class, locationName));
        exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("TestBean3", Integer.class, locationName));
        location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
        //4
        locationName = "some4.test.location.xml";
        imports = new HashSet<BeanReferenceInfo>();
        imports.add(createBean("TestBean1", Object.class, locationName));
        imports.add(createBean("TestBean2", Object.class, locationName));
        imports.add(createBean("TestBean3", Object.class, locationName));
        exports = new HashSet<BeanReferenceInfo>();
        location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
    }

    private void fillLocationsWithRedundantExports() {
        locations = new LinkedList<DependencySorter.Location>();
        //1
        String locationName = "some1.test.location.xml";
        Set<BeanReferenceInfo> imports = new HashSet<BeanReferenceInfo>();
        Set<BeanReferenceInfo> exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("TestBean1", Integer.class, locationName));
        exports.add(createBean("Redundant1", Integer.class, locationName));
        DependencySorter.Location location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
        //2
        locationName = "some2.test.location.xml";
        imports = new HashSet<BeanReferenceInfo>();
        imports.add(createBean("TestBean1", Object.class, locationName));
        exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("TestBean2", Integer.class, locationName));
        exports.add(createBean("Redundant2", Integer.class, locationName));
        location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
        //3
        locationName = "some3.test.location.xml";
        imports = new HashSet<BeanReferenceInfo>();
        imports.add(createBean("TestBean1", Object.class, locationName));
        imports.add(createBean("TestBean2", Object.class, locationName));
        exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("TestBean3", Integer.class, locationName));
        exports.add(createBean("Redundant3", Integer.class, locationName));
        location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
        //4
        locationName = "some4.test.location.xml";
        imports = new HashSet<BeanReferenceInfo>();
        imports.add(createBean("TestBean1", Object.class, locationName));
        imports.add(createBean("TestBean2", Object.class, locationName));
        imports.add(createBean("TestBean3", Object.class, locationName));
        exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("Redundant4", Integer.class, locationName));
        location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
    }


    private void fillLocationsWithSolvableConlict() {
        locations = new LinkedList<DependencySorter.Location>();
        //1
        String locationName = "some1.test.location.xml";
        Set<BeanReferenceInfo> imports = new HashSet<BeanReferenceInfo>();
        Set<BeanReferenceInfo> exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("TestBean1", Integer.class, locationName));
        DependencySorter.Location location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
        //2
        locationName = "some2.test.location.xml";
        imports = new HashSet<BeanReferenceInfo>();
        imports.add(createBean("TestBean1", Object.class, locationName));
        imports.add(createBean("TestBean3", Object.class, locationName));
        exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("TestBean2", Integer.class, locationName));
        location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
        //3
        locationName = "some3.test.location.xml";
        imports = new HashSet<BeanReferenceInfo>();
        imports.add(createBean("TestBean1", Object.class, locationName));
        exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("TestBean3", Integer.class, locationName));
        location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
        //4
        locationName = "some4.test.location.xml";
        imports = new HashSet<BeanReferenceInfo>();
        imports.add(createBean("TestBean1", Object.class, locationName));
        imports.add(createBean("TestBean2", Object.class, locationName));
        imports.add(createBean("TestBean3", Object.class, locationName));
        exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("Redundant4", Integer.class, locationName));
        location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
    }

    private void fillLocationsWithConlicts() {
        locations = new LinkedList<DependencySorter.Location>();
        //1
        String locationName = "some1.test.location.xml";
        Set<BeanReferenceInfo> imports = new HashSet<BeanReferenceInfo>();
        imports.add(createBean("TestBean2", Object.class, locationName));
        Set<BeanReferenceInfo> exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("TestBean1", Integer.class, locationName));
        DependencySorter.Location location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
        //2
        locationName = "some2.test.location.xml";
        imports = new HashSet<BeanReferenceInfo>();
        exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("TestBean2", Integer.class, locationName));
        location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
        //3
        locationName = "some3.test.location.xml";
        imports = new HashSet<BeanReferenceInfo>();
        imports.add(createBean("TestBean1", Object.class, locationName));
        imports.add(createBean("TestBean4", Object.class, locationName));
        exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("TestBean3", Integer.class, locationName));
        location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
        //4
        locationName = "some4.test.location.xml";
        imports = new HashSet<BeanReferenceInfo>();
        imports.add(createBean("TestBean1", Object.class, locationName));
        imports.add(createBean("TestBean2", Object.class, locationName));
        imports.add(createBean("TestBean3", Object.class, locationName));
        exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("TestBean4", Integer.class, locationName));
        exports.add(createBean("Redundant4", Integer.class, locationName));
        location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
        //5
        locationName = "some5.test.location.xml";
        imports = new HashSet<BeanReferenceInfo>();
        imports.add(createBean("TestBean1", Object.class, locationName));
        imports.add(createBean("TestBean2", Object.class, locationName));
        imports.add(createBean("TestBean3", Object.class, locationName));
        exports = new HashSet<BeanReferenceInfo>();
        exports.add(createBean("Redundant5", Integer.class, locationName));
        location = new DependencySorter.Location(locationName, imports, exports);
        locations.add(location);
    }


    private BeanReferenceInfo createBean(String name, Class<?> iface, String location) {
        BeanReferenceInfo importBean = new BeanReferenceInfo();
        importBean.setBeanInterface(iface);
        importBean.setBeanName(name);
        importBean.setLocation(location);
        return importBean;
    }
}
