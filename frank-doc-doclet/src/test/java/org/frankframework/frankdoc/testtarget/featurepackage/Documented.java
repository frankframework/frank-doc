package org.frankframework.frankdoc.testtarget.featurepackage;

import nl.nn.adapterframework.doc.Default;
import nl.nn.adapterframework.doc.IbisDoc;
import org.frankframework.frankdoc.testtarget.wrapper.variables.IMailFileSystem;

public abstract class Documented implements IMailFileSystem<String> {
	public static final String VARIABLE = "my value";
	public void notDocumented() {
	}

	@Default("default value")
	public void withDefaultAnnotation() {
	}

	/**
	 * @ff.default default value
	 */
	public void withDefaultTag(Boolean force) {
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

	/**
	 * Description with {@value #VARIABLE}
	 * @ff.default {@value Documented#VARIABLE}
	 */
	public void withReferencedValue() {
	}

	/**
	 * Description with {@value #REPLY_ADDRESS_FIELDS_DEFAULT}
	 * @ff.default {@value #REPLY_ADDRESS_FIELDS_DEFAULT}
	 */
	public void withReferencedInterfaceValue() {
	}
}
