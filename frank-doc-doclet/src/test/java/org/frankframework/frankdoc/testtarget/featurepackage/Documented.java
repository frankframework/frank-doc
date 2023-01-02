package org.frankframework.frankdoc.testtarget.featurepackage;

import nl.nn.adapterframework.doc.Default;
import nl.nn.adapterframework.doc.IbisDoc;

public class Documented {
	public void notDocumented() {
	}

	@Default("default value")
	public void withDefaultAnnotation() {
	}

	/**
	 * @ff.default default value
	 */
	public void withDefaultTag() {
	}

	/**
	 * My description
	 */
	public void withJavaDoc() {
	}

	@IbisDoc({"1", "My description", "default value"})
	public void withFullIbisDoc() {
	}

	@IbisDoc({"My description", "default value"})
	public void withIbisDocNoOrder() {
	}

	@IbisDoc({"1", "My description"})
	public void withIbisDocNoDefault() {
	}

	@IbisDoc("My description")
	public void withIbisDocNoDefaultNoOrder() {
	}
}
