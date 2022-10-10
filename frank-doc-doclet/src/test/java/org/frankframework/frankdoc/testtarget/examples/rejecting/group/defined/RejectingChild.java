package org.frankframework.frankdoc.testtarget.examples.rejecting.group.defined;

/**
 * This class has to be abstract to cover issue: Cannot resolve the name 'MailListenerCumulativeAttributeGroup' to a(n) 'attribute group' component #101.
 * It should only be visited because it is the parent of GrandChild, not because it implements IInterface.
 * @author martijn
 *
 */
public abstract class RejectingChild extends Parent {
	@Override
	@Deprecated
	public void setRejectedAttribute(String value) {
	}

	@Deprecated
	public void registerC(Nested2 child) {
	}
}
