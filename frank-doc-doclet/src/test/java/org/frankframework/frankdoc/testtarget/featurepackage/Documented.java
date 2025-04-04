package org.frankframework.frankdoc.testtarget.featurepackage;

import org.frankframework.doc.Category;
import org.frankframework.doc.CategoryType;
import org.frankframework.doc.Default;
import org.frankframework.doc.Reintroduce;
import org.frankframework.frankdoc.testtarget.wrapper.variables.IMailFileSystem;


/**
 * Plain extension to {@link DocumentedParent} that can be used directly in configurations.
 *
 * @ff.parameters Any parameters defined on the pipe will be handed to the sender, if this is a ISenderWithParameters.
 */
@Category(CategoryType.BASIC)
public class Documented extends DocumentedParent implements IMailFileSystem<String> {
	public static final String VARIABLE = "my value";
	private boolean transacted = false;
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

	/**
	 * controls the use of transactions
	 */
	public void setTransacted(boolean transacted) {
		this.transacted = transacted;
	}

	@Override
	public boolean isTransacted() {
		return transacted;
	}

	@Override
	@Reintroduce
	public void setSender(ISender sender) {
		super.setSender(sender);
	}

	@Override
	@Reintroduce
	public void setListener(ICorrelatedPullingListener listener) {
		super.setListener(listener);
	}
}
