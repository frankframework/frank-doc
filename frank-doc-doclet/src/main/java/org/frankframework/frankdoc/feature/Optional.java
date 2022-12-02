package org.frankframework.frankdoc.feature;

public class Optional extends AbstractNonValuedFeature {
	private static final Optional INSTANCE = new Optional();

	public static final Optional getInstance() {
		return INSTANCE;
	}

	private Optional() {
		super("nl.nn.adapterframework.doc.Optional", "@ff.optional");
	}
}
