package org.frankframework.frankdoc.feature;

public class Reintroduce extends AbstractNonValuedFeature {
	private static final Reintroduce INSTANCE = new Reintroduce();

	public static Reintroduce getInstance() {
		return INSTANCE;
	}

	private Reintroduce() {
		super("nl.nn.adapterframework.doc.Reintroduce", "@ff.reintroduce");
	}
}
