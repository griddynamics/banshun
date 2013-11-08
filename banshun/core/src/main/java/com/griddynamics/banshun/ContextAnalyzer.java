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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(ContextAnalyzer.class);

    private Map<String, BeanReferenceInfo> exports = new HashMap<String, BeanReferenceInfo>();
    private Map<String, List<BeanReferenceInfo>> imports = new HashMap<String, List<BeanReferenceInfo>>();


    public Map<String, List<BeanReferenceInfo>> getImports() {
        return imports;
    }

    public void addImport(BeanDefinition beanDefinition, String location) throws ClassNotFoundException {
        BeanReferenceInfo importReferenceInfo = parseLookupOrExportRefArg(beanDefinition, location);
        
        putInImports(importReferenceInfo);
    }

    public Map<String, BeanReferenceInfo> getExports() {
        return exports;
    }

    public void addExport(BeanDefinition beanDefinition, String location) throws ClassNotFoundException, BeanCreationException {
        BeanReferenceInfo exportReferenceInfo = getExportReference(beanDefinition, location);

        putInExports(exportReferenceInfo);
    }

    public boolean areThereImportsWithoutExports() {
        boolean allImportsHaveExports = false;
        
        for (String importedBeanName : imports.keySet()) {
            if (!exports.containsKey(importedBeanName)) {
                allImportsHaveExports = true;
                String location = imports.get(importedBeanName).get(0).getLocation();
                log.error("Import without export was found. Bean: {} location: {}", importedBeanName, location);
            }
        }
        
        return allImportsHaveExports;
    }

    public boolean areThereExportsWithoutImport() {
        boolean allExportshaveImports = true;

        for (String exportedBeanName : exports.keySet()) {
            if (!imports.containsKey(exportedBeanName)) {
                allExportshaveImports = false;
                log.warn("Bean {} was exported but never imported", exportedBeanName);
            }
        }

        return allExportshaveImports;
    }

    public boolean areImportsTypesCorrect() {
        boolean importsTypesAreCorrect = true;
        
        for (String exportedBeanName : exports.keySet()) {
            if (imports.containsKey(exportedBeanName)) {
                Class<?> exportedBeanInterface = exports.get(exportedBeanName).getBeanInterface();

                for (BeanReferenceInfo refInfo : imports.get(exportedBeanName)) {
                    Class<?> importedBeanInterface = refInfo.getBeanInterface();
                    
                    if (!importedBeanInterface.isAssignableFrom(exportedBeanInterface)) {
                       importsTypesAreCorrect = false;
                       log.error("Imported bean {} from location {} must implement same interface that appropriate" +
                                 "exported bean {} or subinterface but no superclass or superinterface",
                               new Object[]{exportedBeanName, refInfo.getLocation(), exportedBeanName});
                    }
                }
            }
        }
        
        return importsTypesAreCorrect;
    }


    protected BeanReferenceInfo parseLookupOrExportRefArg(BeanDefinition beanDefinition, String location) throws ClassNotFoundException {
        BeanReferenceInfo shortBeanDefinition = new BeanReferenceInfo();

        String importedBeanName = getTargetBeanName(beanDefinition);
        Class<?> beanInterface = getTargetBeanInterface(beanDefinition);

        shortBeanDefinition.setBeanName(importedBeanName);
        shortBeanDefinition.setBeanInterface(beanInterface);
        shortBeanDefinition.setLocation(location);

        return shortBeanDefinition;
    }

    protected String getTargetBeanName(BeanDefinition beanDefinition) {
        String importedBeanName = null;

        ConstructorArgumentValues.ValueHolder valueHolder = beanDefinition
                .getConstructorArgumentValues().getGenericArgumentValues().get(0);
        Object beanNameValueHolder = valueHolder.getValue();

        if (beanNameValueHolder instanceof RuntimeBeanNameReference) {
            importedBeanName = ((RuntimeBeanNameReference)valueHolder.getValue()).getBeanName();
        } else if (beanNameValueHolder instanceof TypedStringValue) {
            importedBeanName = ((TypedStringValue)valueHolder.getValue()).getValue();
        } else if (beanNameValueHolder instanceof String) {
            importedBeanName = (String)beanNameValueHolder;
        }

        return importedBeanName;
    }

    protected BeanReferenceInfo getExportReference(BeanDefinition beanDefinition, String location) throws ClassNotFoundException {

        ConstructorArgumentValues argumentValues = beanDefinition.getConstructorArgumentValues();

        ValueHolder valueHolder = argumentValues.getArgumentValue(0, BeanDefinitionHolder.class);
        BeanDefinition exportRefBeanDefinition;
        if (valueHolder != null) {
            BeanDefinitionHolder holder = (BeanDefinitionHolder) valueHolder.getValue();
            exportRefBeanDefinition = holder.getBeanDefinition();
        } else {
            exportRefBeanDefinition = (BeanDefinition) (argumentValues.getGenericArgumentValues().get(0)).getValue();
        }

        return parseLookupOrExportRefArg(exportRefBeanDefinition, location);
    }

    //TODO should be private or protected
    public void putInImports(BeanReferenceInfo importRefInfo) {
        String importBeanName = importRefInfo.getBeanName();

        if (imports.containsKey(importBeanName)) {
            imports.get(importBeanName).add(importRefInfo);
        } else {
            List<BeanReferenceInfo> refInfoList = new ArrayList<BeanReferenceInfo>();
            refInfoList.add(importRefInfo);
            imports.put(importBeanName, refInfoList);
        }
    }

    //TODO should be private or protected
    public void putInExports(BeanReferenceInfo exportRefInfo) {
        String exportedBeanName = exportRefInfo.getBeanName();

        if (exports.containsKey(exportedBeanName)) {
            throw new BeanCreationException(String.format(
                    "Double export was defined: %s in context %s. Previous export was in context %s",
                    exportedBeanName, exportRefInfo.getLocation(), exports.get(exportedBeanName).getLocation()));
        } else {
            exports.put(exportedBeanName, exportRefInfo);
        }
    }


    private Class<?> getTargetBeanInterface(BeanDefinition beanDefinition) throws ClassNotFoundException {
        Class<?> beanInterface = null;

        ConstructorArgumentValues.ValueHolder valueHolder = beanDefinition
                .getConstructorArgumentValues().getGenericArgumentValues().get(1);

        Object beanInterfaceName = valueHolder.getValue();

        if (beanInterfaceName instanceof TypedStringValue) {
            beanInterface = Class.forName(((TypedStringValue)beanInterfaceName).getValue());
        } else if (beanInterfaceName instanceof Class) {
            beanInterface = (Class<?>)beanInterfaceName;
        }

        return beanInterface;
    }
}
