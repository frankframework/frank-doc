package org.frankframework.frankdoc;

import org.frankframework.frankdoc.model.FrankAttribute;
import org.frankframework.frankdoc.util.XmlBuilder;

interface AttributeReuseManagerCallback {
	void addAttributeInline(FrankAttribute attribute, XmlBuilder group);
	void addReusableAttribute(FrankAttribute attribute);
	void addReusedAttributeReference(FrankAttribute attribute, XmlBuilder group);
}
