/* 
Copyright 2021 WeAreFrank! 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/

package org.frankframework.frankdoc.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankMethod;

class AttributeExcludedSetter {
	private static Logger log = LogUtil.getLogger(AttributeExcludedSetter.class);

	AttributeExcludedSetter(FrankClass clazz, FrankClassRepository repository) {
		String ignoredInterface = clazz.getJavaDocTag(FrankElement.JAVADOC_IGNORE_TYPE_MEMBERSHIP);
		if(ignoredInterface != null) {
			if(StringUtils.isBlank(ignoredInterface)) {
				log.warn("Javadoc tag {} requires an argument that refers a Java interface", FrankElement.JAVADOC_IGNORE_TYPE_MEMBERSHIP);
			} else {
				log.trace("FrankElement has Javadoc tag {} that refers to interface [{}]", () -> FrankElement.JAVADOC_IGNORE_TYPE_MEMBERSHIP, () -> ignoredInterface);
				FrankClass ignoredInterfaceClass = null;
				try {
					ignoredInterfaceClass = repository.findClass(ignoredInterface);					
				} catch(FrankDocException e) {
					log.warn("Exception when parsing Javadoc tag {} that references Java interface [{}]", FrankElement.JAVADOC_IGNORE_TYPE_MEMBERSHIP, ignoredInterface, e);
				}
				if(ignoredInterfaceClass == null) {
					log.warn("Javadoc tag {} refers to a non-existing Java interface [{}]", FrankElement.JAVADOC_IGNORE_TYPE_MEMBERSHIP, ignoredInterface);
				}
			}
		}
	}

	void updateAttribute(FrankAttribute attribute, FrankMethod method) {
		if(method.getJavaDocTag(FrankAttribute.JAVADOC_NO_FRANK_ATTRIBUTE) != null) {
			log.trace("Attribute [{}] has JavaDoc tag {}, marking as excluded", () -> attribute.getName(), () -> FrankAttribute.JAVADOC_NO_FRANK_ATTRIBUTE);
			attribute.setExcluded(true);
		}
	}
}
