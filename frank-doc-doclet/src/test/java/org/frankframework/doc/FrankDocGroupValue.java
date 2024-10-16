/*
Copyright 2024 WeAreFrank!

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.frankframework.doc;

public enum FrankDocGroupValue {
	@EnumLabel("Pipe")
	PIPE,
	@EnumLabel("Sender")
	SENDER,
	@EnumLabel("Listener")
	LISTENER,
	@EnumLabel("Validator")
	VALIDATOR,
	@EnumLabel("Wrapper")
	WRAPPER,
	@EnumLabel("TransactionalStorage")
	TRANSACTIONAL_STORAGE,
	@EnumLabel("ErrorMessageFormatter")
	ERROR_MESSAGE_FORMATTER,
	@EnumLabel("Batch")
	BATCH,
	@EnumLabel("Monitoring")
	MONITORING,
	@EnumLabel("Job")
	JOB,
	// This label is automatically added to the Module element.
	@EnumLabel("Other")
	OTHER,
}
