package org.frankframework.frankdoc.testtarget.ibisdocref;

import nl.nn.adapterframework.doc.IbisDocRef;

public class Referrer {
	@IbisDocRef("org.frankframework.frankdoc.testtarget.ibisdocref.ChildTarget")
	public void setIbisDocRefClassNoOrderRefersIbisDocOrderDescriptionDefault(String value) {
	}

	@IbisDocRef("org.frankframework.frankdoc.testtarget.ibisdocref.ChildTarget.otherMethod")
	public void setIbisDocReffMethodNoOrderRefersIbisDocOrderDescriptionDefault(String value) {
	}

	@IbisDocRef({"10", "org.frankframework.frankdoc.testtarget.ibisdocref.ChildTarget"})
	public void setIbisDocRefClassWithOrderRefersIbisDocOrderDescriptionDefaultInherited(String value) {
	}

	@IbisDocRef({"100", "org.frankframework.frankdoc.testtarget.ibisdocref.ChildTarget"})
	public void setAttributeWithIbisDocRefReferringJavadoc(String value) {
	}

	@IbisDocRef({"110", "org.frankframework.frankdoc.testtarget.ibisdocref.ChildTarget"})
	public void setAttributeWithIbisDocRefThatGivesPreferenceToIbisDocDescriptionOverJavadoc(String value) {
	}

	@IbisDocRef({"120", "org.frankframework.frankdoc.testtarget.ibisdocref.ChildTarget"})
	public void setAttributeWithIbisDocRefReferringIbisDocWithoutDescriptionButWithJavadoc(String value) {
	}

	@IbisDocRef("org.frankframework.frankdoc.testtarget.ibisdocref.ChildTarget")
	public void setIbisDocRefRefersJavaDocDefault(String value) {
	}
}