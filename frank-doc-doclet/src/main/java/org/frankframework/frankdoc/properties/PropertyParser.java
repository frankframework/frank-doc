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

import java.util.ArrayList;
import java.util.List;

public class PropertyParser implements IPropertyParser {

	private static final String START_OR_END_GROUP_TOKEN = "####";
	private static final String COMMENT_TOKEN = "##";
	private static final String PROPERTY_TOKEN = "#";

	private static final String[] ALLOWED_FLAGS = new String[]{"Deprecated", "Generated"};

	public List<Group> parse(String fileContent) {
		ArrayList<Group> groups = new ArrayList<>();

		Group currentGroup = new Group();
		Property currentProperty = new Property();

		final String[] lines = fileContent.split("\n");
		for (String line : lines) {
			line = line.trim();

			if (line.isEmpty() || line.charAt(0) == '!') {
				continue;
			}

			if (line.startsWith(START_OR_END_GROUP_TOKEN)) {
				if (!currentGroup.getProperties().isEmpty()) {
					groups.add(currentGroup);
				}

				currentGroup = new Group();
				currentGroup.setName(line.substring(START_OR_END_GROUP_TOKEN.length()).trim());
				continue;
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
				continue;
			}

			if (line.startsWith(PROPERTY_TOKEN)) {
				line = line.substring(PROPERTY_TOKEN.length()).trim();
			}

			final String[] keyValue = line.split("=");

			currentProperty.setName(keyValue[0]);
			if (keyValue.length > 1) {
				currentProperty.setDefaultValue(keyValue[1]);
			}
			currentGroup.addProperty(currentProperty);
			currentProperty = new Property();
		}

		return groups;
	}

}
