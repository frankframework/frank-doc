/* 
Copyright 2021, 2022 WeAreFrank! 

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
	static final String MODULE_ELEMENT_DESCRIPTION = "Root element for XML files that contain multiple adapters and/or jobs scheduled for periodic execution. Such an XML file is included as entity reference in another XML file. The element does not influence the behavior of Frank configs.";
	public static final String IGNORE_COMPATIBILITY_MODE = "ignoreInCompatibilityMode";
	public static final String JAVA_DOC_TAG_MANDATORY = "@ff.mandatory";
}
