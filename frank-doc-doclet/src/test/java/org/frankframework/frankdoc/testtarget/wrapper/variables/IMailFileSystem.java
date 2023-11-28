package org.frankframework.frankdoc.testtarget.wrapper.variables;

public interface IMailFileSystem<M> {

	/**
	 *  indicates implementing object is under transaction control, using XA-transactions
	 */
	public boolean isTransacted();

	String REPLY_ADDRESS_FIELDS_DEFAULT = "replyAddressFieldsDefault";
}
