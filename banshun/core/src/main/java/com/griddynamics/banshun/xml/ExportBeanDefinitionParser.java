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

import com.griddynamics.banshun.ExportRef;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.ObjectUtils;
import org.w3c.dom.Element;

import static com.griddynamics.banshun.xml.ParserUtils.*;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;


public class ExportBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    /**
     * String to be appended to the exported bean name.
     */
    private static final String BEAN_NAME_SUFFIX = "-export-ref";


    @Override
    protected String getBeanClassName(Element element) {
        return Void.class.getCanonicalName();
    }

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        return element.getAttribute(REF_ATTR)
                + "$export"
                + BeanFactoryUtils.GENERATED_BEAN_NAME_SEPARATOR
                + ObjectUtils.getIdentityHexString(definition);
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        BeanDefinitionRegistry registry = parserContext.getRegistry();

        String exportInterface = element.getAttribute(INTERFACE_ATTR);
        if (isBlank(exportInterface)) {
            return;
        }

        String exportBeanRef = element.getAttribute(REF_ATTR);
        if (isBlank(exportBeanRef)) {
            return;
        }

        String rootName = element.getAttribute(ROOT_ATTR);
        if (isBlank(rootName)) {
            rootName = DEFAULT_ROOT_FACTORY_NAME;
        }

        String exportName = element.getAttribute(NAME_ATTR);
        if (isBlank(exportName)) {
            exportName = exportBeanRef;
        }

        String exportRefName = exportName + BEAN_NAME_SUFFIX;
        if (registry.containsBeanDefinition(exportRefName)) {
            throw new BeanCreationException("Registry already contains bean with name: " + exportRefName);
        }

        ConstructorArgumentValues exportBeanConstructorArgValues = new ConstructorArgumentValues();
        exportBeanConstructorArgValues.addGenericArgumentValue(exportName);
        exportBeanConstructorArgValues.addGenericArgumentValue(findClass(
                exportInterface,
                exportBeanRef,
                parserContext.getReaderContext().getResource().getDescription()
        ));

        AbstractBeanDefinition exportBeanDef = rootBeanDefinition(ExportRef.class).getRawBeanDefinition();
        exportBeanDef.setConstructorArgumentValues(exportBeanConstructorArgValues);

        ConstructorArgumentValues voidBeanConstructorArgValues = new ConstructorArgumentValues();
        voidBeanConstructorArgValues.addGenericArgumentValue(exportBeanDef, ExportRef.class.getName());

        AbstractBeanDefinition voidBeanDef = rootBeanDefinition(Void.class).getRawBeanDefinition();
        voidBeanDef.setFactoryBeanName(rootName);
        voidBeanDef.setFactoryMethodName("export");
        voidBeanDef.setLazyInit(false);
        voidBeanDef.setScope(SCOPE_SINGLETON);
        voidBeanDef.setConstructorArgumentValues(voidBeanConstructorArgValues);
//        voidBeanDefinition.setDependsOn(new String[] { exportBeanRef }); TODO ?

        registry.registerBeanDefinition(exportRefName, voidBeanDef);
    }

}
