package org.frankframework.frankdoc.testtarget.packageprivate.override;

public abstract class AbstractParent<M> {

	public String keyField;
	private String destinationName;

	private Boolean forceMessageIdAsCorrelationId = null;

	public M var;

	/** Primary key field of the table, used to identify messages. <a href="https://www.eclipse.org/paho/files/javadoc" target="_blank">link</a>.
	 * use <code>XML</code> shizzle. */
	public void setKeyField(String fieldname) {
		keyField = fieldname;
	}

	public String getKeyField() {
		return keyField;
	}

	public Boolean getForceMessageIdAsCorrelationId() {
		return forceMessageIdAsCorrelationId;
	}

	/**
	 * JavaDoc of AbstractParent.setAlarm.
	 * @param b
	 */
	void setAlarm(boolean b) {
	}

	/** Name of the destination (queue or topic) to use */
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	/**
	 * By default text from Parent.
	 * @ff.default false
	 */
	public void setForceMessageIdAsCorrelationId(Boolean force){
	}
}
