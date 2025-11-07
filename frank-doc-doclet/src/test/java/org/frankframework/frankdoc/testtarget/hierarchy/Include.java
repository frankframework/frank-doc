package org.frankframework.frankdoc.testtarget.hierarchy;

import lombok.Getter;

/**
 * Dummy element to trigger Frank!Doc to allow including other files in the right places
 */
public class Include {

	private @Getter String ref;

	/** reference to a configuration to be included in the current. */
	public void setRef(String ref) {
		this.ref = ref;
	}
}
