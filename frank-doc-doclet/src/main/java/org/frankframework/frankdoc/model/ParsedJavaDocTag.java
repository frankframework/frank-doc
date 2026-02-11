/*
Copyright 2021, 2024 WeAreFrank!

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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.frankframework.frankdoc.Utils;
import org.frankframework.frankdoc.util.FrankDocThrowingFunction;
import org.frankframework.frankdoc.wrapper.FrankDocException;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ParsedJavaDocTag {
	private static final String QUOTE = "\"";

	private final @Getter String name;
	private final @Getter String description;

	static ParsedJavaDocTag getInstance(String javaDocTagParameter, FrankDocThrowingFunction valueSubstitutor) throws FrankDocException {
		if(StringUtils.isAllBlank(javaDocTagParameter)) {
			throw new FrankDocException("Tag has no arguments", null);
		}
		ParsedJavaDocTag raw = null;
		// The doclet API already trimmed the argument. We do not have to care about leading spaces.
		if(javaDocTagParameter.startsWith(QUOTE)) {
			raw = getInstanceQuoteDelimited(javaDocTagParameter);
		} else {
			raw = getInstanceSpaceDelimited(javaDocTagParameter);
		}
		if (raw.description == null) {
			return new ParsedJavaDocTag(valueSubstitutor.apply(raw.name), null);
		} else {
			return new ParsedJavaDocTag(valueSubstitutor.apply(raw.name), valueSubstitutor.apply(raw.description));
		}
	}

	private static ParsedJavaDocTag getInstanceQuoteDelimited(String javaDocTagParameter) throws FrankDocException {
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

	private static ParsedJavaDocTag getInstanceSpaceDelimited(String javaDocTagParameter) throws FrankDocException {
		int idx = ParsedJavaDocTag.getNameDescriptionSplitIndex(javaDocTagParameter);
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

	private static int getNameDescriptionSplitIndex(String javadocTagParameter) throws FrankDocException {
		int idx = 0;
		while (idx < javadocTagParameter.length()) {
			idx = ParsedJavaDocTag.getIndexFirstMatchFromIndex(javadocTagParameter, idx, " ", "\t", Utils.JAVADOC_VALUE_START_DELIMITER);
			if (idx < 0) {
				// Not found
				return idx;
			}
			if (Character.isWhitespace(javadocTagParameter.charAt(idx))) {
				// Found the first space
				return idx;
			}
			// We do not have the space that splits the name and the description, but a {@value ... } pattern
			// Skip that.
			idx = javadocTagParameter.indexOf(Utils.JAVADOC_SUBSTITUTION_PATTERN_STOP_DELIMITER, idx);
			if (idx == -1) {
				throw new FrankDocException(String.format("Value substitution pattern %s not finished by %s",
					Utils.JAVADOC_VALUE_START_DELIMITER,
					Utils.JAVADOC_SUBSTITUTION_PATTERN_STOP_DELIMITER), null);
			}
			idx = idx + 1;
		}
		return -1;
	}

	private static int getIndexFirstMatchFromIndex(String subject, int fromIndex, String ...searchItemsArg) {
		List<String> searchItems = Arrays.asList(searchItemsArg);
		return searchItems.stream().map(item -> subject.indexOf(item, fromIndex))
			.filter(i -> i >= 0)
			.min(Integer::compare)
			.orElse(-1);
	}

	private ParsedJavaDocTag(String name, String description) {
		this.name = name;
		this.description = description;
	}

	@Override
	public String toString() {
		return "Name: " + name + (description == null ? "" : ", description: " + description);
	}
}
