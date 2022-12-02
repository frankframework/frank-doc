package org.frankframework.frankdoc.feature;

public class Deprecated extends AbstractNonValuedFeature {
	private static final Deprecated INSTNCE = new Deprecated();

	public static Deprecated getInstance() {
		return INSTNCE;
	}

	private Deprecated() {
		super("java.lang.Deprecated", "@deprecated");
	}
}
