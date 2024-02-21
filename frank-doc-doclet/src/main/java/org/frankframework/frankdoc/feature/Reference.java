/*
Copyright 2022, 2023 WeAreFrank!

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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankDocletConstants;
import org.frankframework.frankdoc.wrapper.FrankMethod;

public class Reference {
	private static Logger log = LogUtil.getLogger(Reference.class);
	private static final String JAVADOC_ATTRIBUTE_REF = "@ff.ref";
	private static final String REFER_TO = "org.frankframework.doc.ReferTo";

	private final FrankClassRepository classRepository;

	public Reference(FrankClassRepository classRepository) {
		this.classRepository = classRepository;
	}

	public FrankMethod valueOf(FrankMethod method) {
		String resultAsString = method.getJavaDocTag(JAVADOC_ATTRIBUTE_REF);
		if(resultAsString == null) {
			resultAsString = getReferToAnnotation(method);
		}
		if(resultAsString != null) {
			if(StringUtils.isBlank(resultAsString)) {
				log.error("JavaDoc tag {} should have a full class name or full method name as argument", JAVADOC_ATTRIBUTE_REF);
			} else {
				FrankMethod referred = getReferredMethod(resultAsString, method);
				if(referred == null) {
					log.error("Referred method [{}] does not exist, as specified at location: [{}]", resultAsString, method);
				} else {
					return referred;
				}
			}
		}
		FrankAnnotation ibisDocRef = method.getAnnotation(FrankDocletConstants.IBISDOCREF);
		if(ibisDocRef != null) {
			ParsedIbisDocRef parsedIbisDocRef = parseIbisDocRef(ibisDocRef, method);
			return parsedIbisDocRef.getReferredMethod();
		}
		return null;
	}

	private String getReferToAnnotation(FrankMethod method) {
		FrankAnnotation referTo = method.getAnnotation(REFER_TO);
		if(referTo != null) {
			try {
				return (String) referTo.getValue();
			} catch(FrankDocException e) {
				log.error("Could not get value of annotation [{}] on method [{}]", referTo.getName(), method.toString(), e);
			}
		}
		return null;
	}

	private ParsedIbisDocRef parseIbisDocRef(FrankAnnotation ibisDocRef, FrankMethod originalMethod) {
		ParsedIbisDocRef result = new ParsedIbisDocRef();
		result.setHasOrder(false);
		String[] values = null;
		try {
			values = (String[]) ibisDocRef.getValue();
		} catch(FrankDocException e) {
			log.error("IbisDocRef annotation did not have a value", e);
			return result;
		}
		String methodString = null;
		if (values.length == 1) {
			methodString = values[0];
		} else if (values.length == 2) {
			methodString = values[1];
			try {
				result.setOrder(Integer.parseInt(values[0]));
				result.setHasOrder(true);
			} catch (Throwable t) {
				final String[] finalValues = values;
				log.error("Could not parse order in @IbisDocRef annotation: [{}]", () -> finalValues[0]);
			}
		}
		else {
			log.error("Too many or zero parameters in @IbisDocRef annotation on method: [{}].[{}]", () -> originalMethod.getDeclaringClass().getName(), () -> originalMethod.getName());
			return null;
		}
		try {
			result.setReferredMethod(getReferredMethod(methodString, originalMethod));
		} catch(Exception e) {
			log.error("@IbisDocRef on [{}].[{}] annotation references invalid method [{}], ignoring @IbisDocRef annotation",
					originalMethod.getDeclaringClass().getName(), originalMethod.getName(), methodString);
			return null;
		}
		return result;
	}

	private FrankMethod getReferredMethod(String methodString, FrankMethod originalMethod) {
		String lastNameComponent = methodString.substring(methodString.lastIndexOf(".") + 1).trim();
		char firstLetter = lastNameComponent.toCharArray()[0];
		String fullClassName = methodString;
		String methodName = lastNameComponent;
		if (Character.isLowerCase(firstLetter)) {
			int index = methodString.lastIndexOf(".");
			fullClassName = methodString.substring(0, index);
		} else {
			methodName = originalMethod.getName();
		}
		return getParentMethod(fullClassName, methodName);
	}

	private FrankMethod getParentMethod(String className, String methodName) {
		try {
			FrankClass parentClass = classRepository.findClass(className);
			if(parentClass == null) {
				log.error("Class {} is unknown", className);
				return null;
			}
			for (FrankMethod parentMethod : parentClass.getDeclaredAndInheritedMethods()) {
				if (parentMethod.getName().equals(methodName)) {
					return parentMethod;
				}
			}
			return null;
		} catch (FrankDocException e) {
			log.error("Super class [{}] was not found!", className, e);
			return null;
		}
	}
}
