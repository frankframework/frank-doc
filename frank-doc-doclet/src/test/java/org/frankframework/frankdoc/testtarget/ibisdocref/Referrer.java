package org.frankframework.frankdoc.testtarget.ibisdocref;

import nl.nn.adapterframework.doc.IbisDocRef;
import nl.nn.adapterframework.doc.ReferTo;

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

	/**
	 * @ff.ref org.frankframework.frankdoc.testtarget.ibisdocref.ChildTarget
	 * 
	 */
	public void setFfReferInheritedDescription(String value) {
	}

	@ReferTo(ChildTarget.class)
	public void setReferToInheritedDescription(String value) {
	}

	@ReferTo(ChildTargetParameterized.class)
	public void setReferToParameterizedType(String value) {
	}
}
