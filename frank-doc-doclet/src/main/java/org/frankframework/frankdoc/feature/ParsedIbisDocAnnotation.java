package org.frankframework.frankdoc.feature;

import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankDocException;

import lombok.Getter;

class ParsedIbisDocAnnotation {
	static final String IBISDOC = "nl.nn.adapterframework.doc.IbisDoc";

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
