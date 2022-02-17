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
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankEnumConstant;

import lombok.Getter;

public class AttributeEnumValue {
	private static Logger log = LogUtil.getLogger(AttributeEnumValue.class);

	private static final String ENUM_LABEL = "nl.nn.adapterframework.doc.EnumLabel";

	private @Getter boolean explicitLabel = false;
	private @Getter String javaTag;
	private @Getter String label;
	private @Getter String description;
	private @Getter boolean deprecated = false;

	AttributeEnumValue(FrankEnumConstant c) {
		this.javaTag = c.getName();
		this.label = this.javaTag;
		FrankAnnotation annotation = c.getAnnotation(ENUM_LABEL);
		String annotationValue = null;
		if(annotation != null) {
			try {
				annotationValue = (String) annotation.getValue();
			} catch(FrankDocException e) {
				log.error("Could not parse annotation value of {}", ENUM_LABEL, e);
			}			
		}
		if(! StringUtils.isBlank(annotationValue)) {
			this.explicitLabel = true;
			this.label = annotationValue;
		}
		String javaDoc = c.getJavaDoc();
		if(! StringUtils.isBlank(javaDoc)) {
			this.description = javaDoc;
		}
		try {
			if(Feature.DEPRECATED.isSetOn(c)) {
				this.deprecated = true;
			}
		} catch(FrankDocException e) {
			log.error("Could not parse Java annotation or JavaDoc tag for enum constant [{}]", c.getName(), e);
		}
	}
}
