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

import org.frankframework.frankdoc.feature.Mandatory;

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

	static MandatoryStatus of(Mandatory.Value mandatoryValue, boolean optionalValue) {
		if(optionalValue || (mandatoryValue == null)) {
			return MandatoryStatus.OPTIONAL;
		}
		if(mandatoryValue == Mandatory.Value.IGNORE_COMPATIBILITY) {
			return MandatoryStatus.BECOMES_MANDATORY;
		} else {
			return MandatoryStatus.MANDATORY;
		}
	}
}
