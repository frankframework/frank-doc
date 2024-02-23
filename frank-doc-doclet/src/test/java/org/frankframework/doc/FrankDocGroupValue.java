package org.frankframework.doc;

public enum FrankDocGroupValue {
	@EnumLabel("Pipes")
	PIPES,
	@EnumLabel("Senders")
	SENDERS,
	@EnumLabel("Listeners")
	LISTENERS,
	@EnumLabel("Validators")
	VALIDATORS,
	@EnumLabel("Wrappers")
	WRAPPERS,
	@EnumLabel("TransactionalStorages")
	TRANSACTIONAL_STORAGES,
	@EnumLabel("ErrorMessageFormatters")
	ERROR_MESSAGE_FORMATTERS,
	@EnumLabel("Batch")
	BATCH,
	@EnumLabel("Monitoring")
	MONITORING,
	@EnumLabel("Job")
	JOB
	// We omit the others group to simplify the implementation.
	// You cannot explicitly assign FrankElement's to the others group
}
