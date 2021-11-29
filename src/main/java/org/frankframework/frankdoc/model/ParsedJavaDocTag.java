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

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;

import lombok.Getter;

public class ParsedJavaDocTag {
	private static Logger log = LogUtil.getLogger(ParsedJavaDocTag.class);

	private static String QUOTE = "\"";

	private final @Getter String name;
	private final @Getter String description;

	static ParsedJavaDocTag getInstance(String javaDocTagParameter) {
		// The doclet API already trimmed the argument. We do not have to care about leading spaces.
		if(javaDocTagParameter.startsWith(QUOTE)) {
			return getInstanceQuoteDelimited(javaDocTagParameter);
		}
		return getInstanceSpaceDelimited(javaDocTagParameter);
	}

	private static ParsedJavaDocTag getInstanceQuoteDelimited(String javaDocTagParameter) {
		int startQuoteIdx = javaDocTagParameter.indexOf(QUOTE);
		int endQuoteIdx = javaDocTagParameter.indexOf(QUOTE, startQuoteIdx+1);
		if(endQuoteIdx < 0) {
			log.error("Name of parameter or forward is quoted, but there is no end quote, text is [{}]", javaDocTagParameter);
			return getInstanceSpaceDelimited(javaDocTagParameter);
		}
		String name = javaDocTagParameter.substring(startQuoteIdx + 1, endQuoteIdx);
		if(endQuoteIdx >= (javaDocTagParameter.length() - 1)) {
			return new ParsedJavaDocTag(name, null);
		}
		String description = javaDocTagParameter.substring(endQuoteIdx + 1).trim();
		return new ParsedJavaDocTag(name, description);
	}

	private static ParsedJavaDocTag getInstanceSpaceDelimited(String javaDocTagParameter) {
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
