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

package org.frankframework.frankdoc.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.frankframework.frankdoc.Constants;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankMethod;

public enum MandatoryStatus {
	/**
	 * Attribute or config child is mandatory. It should also be mandatory when a config is validated.
	 */
	MANDATORY,

	/**
	 * Attribute or config child is mandatory, but only in the text editor of Frank developers.
	 * For backward compatibility, the Frank!Framework should accept configs without the
	 * config child or attribute.
	 */
	BECOMES_MANDATORY,

	/**
	 * Attribute or config child is optional.
	 */
	OPTIONAL;

	private static final Set<String> IGNORE_COMPATIBILITY = new HashSet<>(Arrays.asList("true", Constants.IGNORE_COMPATIBILITY_MODE));
	private static final Set<String> DONT_IGNORE_COMPATIBILITY = new HashSet<>(Arrays.asList("", "false"));

	static MandatoryStatus fromMethod(FrankMethod method) throws FrankDocException {
		if(Feature.MANDATORY.isEffectivelySetOn(method)) {
			return fromMethodProperties(Feature.OPTIONAL.isEffectivelySetOn(method), Feature.MANDATORY.isEffectivelySetOn(method), Feature.MANDATORY.valueOf(method));
		} else {
			return fromMethodProperties(Feature.OPTIONAL.isEffectivelySetOn(method), Feature.MANDATORY.isEffectivelySetOn(method), null);
		}
	}

	private static MandatoryStatus fromMethodProperties(boolean isOptional, boolean isMandatory, String valueOfMandatory) throws FrankDocException {
		if(isOptional) {
			return MandatoryStatus.OPTIONAL;
		}
		if(isMandatory) {
			if((valueOfMandatory == null) || DONT_IGNORE_COMPATIBILITY.contains(valueOfMandatory)) {
				return MandatoryStatus.MANDATORY;
			} else if(IGNORE_COMPATIBILITY.contains(valueOfMandatory)) {
				return MandatoryStatus.BECOMES_MANDATORY;
			} else {
				throw new FrankDocException(String.format("Unknown value of JavaDoc tag %s: [%s]", Constants.JAVA_DOC_TAG_MANDATORY, valueOfMandatory), null);
			}
		}
		return MandatoryStatus.OPTIONAL;
	}
}
