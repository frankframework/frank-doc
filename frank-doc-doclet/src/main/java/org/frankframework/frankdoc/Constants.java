/*
Copyright 2021, 2022, 2024, 2025 WeAreFrank!

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

package org.frankframework.frankdoc;

public class Constants {
	static final String MODULE_ELEMENT_NAME = "Module";
	static final String MODULE_ELEMENT_DESCRIPTION = "Wrapper element to help split up large configuration files into smaller valid XML files. It may be used as root tag when an XML file contains multiple adapters and/or jobs. The Module element itself does not influence the behavior of Frank configurations.";
	static final String MODULE_ELEMENT_FRANK_DOC_GROUP = "Other";

	static final String PIPELINE_PART_ELEMENT_NAME = "PipelinePart";
	static final String PIPELINE_PART_ELEMENT_DESCRIPTION = "Wrapper element to help create reusable parts of a pipeline";
	static final String PIPE_ELEMENT_GROUP_BASE = "PipeElementGroupBase";

	public static final String FRANK_DOC_GROUP_VALUES_PACKAGE = "org.frankframework.doc.";

	public static final String CONFIG_WARNING_ELEMENT_NAME = "ConfigWarning";
	public static final String WARNING_ELEMENT_TYPE_NAME = "WarningType";

	private Constants() {
		// Private constructor to prevent instance creation
	}
}
