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

import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import static com.griddynamics.banshun.xml.ParserUtils.*;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;


public class ImportBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected String getBeanClassName(Element element) {
        return element.getAttribute(INTERFACE_ATTR);
    }

    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

        String exportInterface = element.getAttribute(INTERFACE_ATTR);
        if (isBlank(exportInterface)) {
            return;
        }

        String externalName = element.getAttribute(ID_ATTR);
        if (isBlank(externalName)) {
            return;
        }

        String rootName = element.getAttribute(ROOT_ATTR);
        if (isBlank(rootName)) {
            rootName = DEFAULT_ROOT_FACTORY_NAME;
        }

        ConstructorArgumentValues constructorArgValues = new ConstructorArgumentValues();
        constructorArgValues.addGenericArgumentValue(externalName);
        constructorArgValues.addGenericArgumentValue(findClass(
                exportInterface,
                element.getAttribute(ID_ATTR),
                parserContext.getReaderContext().getResource().getDescription()
        ));

        AbstractBeanDefinition beanDef = builder.getRawBeanDefinition();
        beanDef.setFactoryBeanName(rootName);
        beanDef.setFactoryMethodName("lookup");
        beanDef.setConstructorArgumentValues(constructorArgValues);
        beanDef.setLazyInit(true);
        beanDef.setScope(SCOPE_SINGLETON);
    }
}
