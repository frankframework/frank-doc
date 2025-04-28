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

package org.frankframework.frankdoc.properties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class PropertyParser {

	private static final String START_OR_END_GROUP_TOKEN = "####";
	private static final String COMMENT_TOKEN = "##";
	private static final String PROPERTY_TOKEN = "#";

	private static final String[] ALLOWED_FLAGS = new String[]{"Deprecated", "Generated"};

	private final ArrayList<Group> groups = new ArrayList<>();

	private Group currentGroup = new Group();
	private Property currentProperty = new Property();

	public List<Group> parse(Reader reader) {
		try (BufferedReader br = new BufferedReader(reader)) {
			while (br.ready()) {
				String line = br.readLine().trim();
				parseLine(line);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return groups;
	}

	private void parseLine(String line) {
		if (line.isEmpty() || line.charAt(0) == '!') {
			return;
		}

		if (line.startsWith(START_OR_END_GROUP_TOKEN)) {
			if (!currentGroup.getProperties().isEmpty()) {
				groups.add(currentGroup);
			}

			currentGroup = new Group();
			currentGroup.setName(line.substring(START_OR_END_GROUP_TOKEN.length()).trim());
			return;
		}

		if (line.startsWith(COMMENT_TOKEN)) {
			String description = line.substring(COMMENT_TOKEN.length()).trim();

			for (String flag : ALLOWED_FLAGS) {
				final String match = "[" + flag + "]";

				if (description.contains(match)) {
					currentProperty.getFlags().add(flag);
					description = description.replace(match, "").trim();
				}
			}

			if (currentProperty.getDescription() != null && !currentProperty.getDescription().isEmpty()) {
				currentProperty.setDescription(currentProperty.getDescription() + "\n" + description);
			} else {
				currentProperty.setDescription(description);
			}
			return;
		}

		if (line.startsWith(PROPERTY_TOKEN)) {
			line = line.substring(PROPERTY_TOKEN.length()).trim();
		}

		final String[] keyValue = line.split("=", 2);

		currentProperty.setName(keyValue[0]);
		if (keyValue.length > 1) {
			currentProperty.setDefaultValue(keyValue[1]);
		}
		currentGroup.addProperty(currentProperty);
		currentProperty = new Property();
	}

}
