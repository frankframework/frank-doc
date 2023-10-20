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

import lombok.Getter;
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankDocException;

class ParsedIbisDocAnnotation {
	private @Getter String description;
	private @Getter String defaultValue;

	ParsedIbisDocAnnotation(FrankAnnotation ibisDoc) throws FrankDocException {
		String[] ibisDocValues = null;
		try {
			ibisDocValues = (String[]) ibisDoc.getValue();
		} catch(FrankDocException e) {
			throw new FrankDocException("Could not parse FrankAnnotation of @IbisDoc", e);
		}
		boolean isIbisDocHasOrder = false;
		try {
			Integer.parseInt(ibisDocValues[0]);
			isIbisDocHasOrder = true;
		} catch (NumberFormatException e) {
			isIbisDocHasOrder = false;
		}
		if (isIbisDocHasOrder) {
			if(ibisDocValues.length > 1) {
				description = ibisDocValues[1];
			}
			if (ibisDocValues.length > 2) {
				defaultValue = ibisDocValues[2];
			}
		} else {
			description = ibisDocValues[0];
			if (ibisDocValues.length > 1) {
				defaultValue = ibisDocValues[1];
			}
		}
	}
}
