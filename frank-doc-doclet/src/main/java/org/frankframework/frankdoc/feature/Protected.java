package org.frankframework.frankdoc.feature;

public class Protected extends AbstractNonValuedFeature {
	private static final Protected INSTANCE = new Protected();

	public static Protected getInstance() {
		return INSTANCE;
	}

	private Protected() {
		super("nl.nn.adapterframework.doc.Protected", "@ff.protected");
	}
}
