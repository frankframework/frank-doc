package org.frankframework.frankdoc.feature;

import org.frankframework.frankdoc.wrapper.FrankMethod;

import lombok.Getter;
import lombok.Setter;

class ParsedIbisDocRef {
	private @Getter @Setter boolean hasOrder;
	private @Getter @Setter int order;
	private @Getter @Setter FrankMethod referredMethod;
}
