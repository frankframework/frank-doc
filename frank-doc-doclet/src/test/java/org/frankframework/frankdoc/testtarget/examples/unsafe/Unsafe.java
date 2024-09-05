package org.frankframework.frankdoc.testtarget.examples.unsafe;

public class Unsafe {

	/**
	 * Documentation for this unsafe attribute.
	 */
	@org.frankframework.doc.Unsafe
	public void setUnsafeAttribute(String attribute) {
	}

	/**
	 * Documentation for this unsafe attribute without a dot
	 */
	@org.frankframework.doc.Unsafe
	public void setUnsafeAttributeWithoutADot(String attribute) {
	}

	/**
	 * Documentation for this safe attribute.
	 */
	public void setSafeAttribute(String attribute) {
	}

}
