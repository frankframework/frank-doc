/* 
Copyright 2022 WeAreFrank! 

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

package org.frankframework.frankdoc.feature;

import org.frankframework.frankdoc.model.DeprecationInfo;
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankMethod;

public class Deprecated extends AbstractNonValuedFeature {
	private static final Deprecated INSTANCE = new Deprecated();
	private static final String DEPRECATED_ANNOTATION_CLASSNAME = "java.lang.Deprecated";
	private static final String CONFIG_WARNING_ANNOTATION_CLASSNAME = "org.frankframework.configuration.ConfigurationWarning";

	public static Deprecated getInstance() {
		return INSTANCE;
	}

	private Deprecated() {
		super(DEPRECATED_ANNOTATION_CLASSNAME, "@deprecated");
	}

	public DeprecationInfo getInfo(FrankClass clazz) {
		FrankAnnotation deprecationAnnotation = clazz.getAnnotation(DEPRECATED_ANNOTATION_CLASSNAME);
		FrankAnnotation configWarningAnnotation = clazz.getAnnotation(CONFIG_WARNING_ANNOTATION_CLASSNAME);

		return getInfo(deprecationAnnotation, configWarningAnnotation);
	}

	public DeprecationInfo getInfo(FrankMethod method) {
		FrankAnnotation deprecationAnnotation = method.getAnnotation(DEPRECATED_ANNOTATION_CLASSNAME);
		FrankAnnotation configWarningAnnotation = method.getAnnotation(CONFIG_WARNING_ANNOTATION_CLASSNAME);

		return getInfo(deprecationAnnotation, configWarningAnnotation);
	}

	private DeprecationInfo getInfo(FrankAnnotation deprecationAnnotation, FrankAnnotation configWarningAnnotation) {
		if (deprecationAnnotation != null) {
			boolean forRemoval = false;
			String since = null;

			Object forRemovalObject = deprecationAnnotation.getValueOf("forRemoval");
			Object sinceObject = deprecationAnnotation.getValueOf("since");

			if (forRemovalObject instanceof Boolean value) {
				forRemoval = value;
			}

			if (sinceObject instanceof String value) {
				since = value;
			}

			String configurationWarning = null;
			if (configWarningAnnotation != null) {
				Object valueObject = configWarningAnnotation.getValue();

				if (valueObject instanceof String value) {
					configurationWarning = value;
				}
			}

			return new DeprecationInfo(forRemoval, since, configurationWarning);
		}

		return null;
	}

}
