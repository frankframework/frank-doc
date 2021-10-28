/* 
Copyright 2021 WeAreFrank! 

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

public class ParsedJavaDocTag {
	private final @Getter String name;
	private final @Getter String description;

	static ParsedJavaDocTag getInstance(String javaDocTagParameter) {
		int idx = javaDocTagParameter.indexOf(" ");
		if(idx < 0) {
			return new ParsedJavaDocTag(javaDocTagParameter, null);
		}
		String name = javaDocTagParameter.substring(0, idx).trim();
		String description = javaDocTagParameter.substring(idx).trim();
		if(description.isEmpty()) {
			return new ParsedJavaDocTag(name, null);
		}
		return new ParsedJavaDocTag(name, description);
	}

	private ParsedJavaDocTag(String name, String description) {
		this.name = name;
		this.description = description;
	}
}
