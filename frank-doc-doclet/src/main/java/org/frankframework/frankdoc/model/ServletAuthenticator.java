/*
Copyright 2025 WeAreFrank!

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
package org.frankframework.frankdoc.model;

import lombok.Getter;

public class ServletAuthenticator {

	private @Getter String simpleName;
	private @Getter String fullName;
	private @Getter String description;

	public ServletAuthenticator(final String simpleName, final String fullName, final String description) {
		this.simpleName = simpleName;
		this.fullName = fullName;
		this.description = description;
	}

}
