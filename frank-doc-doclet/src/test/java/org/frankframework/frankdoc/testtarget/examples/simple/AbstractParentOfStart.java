package org.frankframework.frankdoc.testtarget.examples.simple;

import org.frankframework.doc.FrankDocGroup;
import org.frankframework.doc.FrankDocGroupValue;

//Should not appear as element in the XSD because it is abstract.
//Should appear as attribute group because it has an attribute
@FrankDocGroup(FrankDocGroupValue.WRAPPER)
public abstract class AbstractParentOfStart {
	public void setAbstractParentOfStartAttribute(String value) {
	}
}
