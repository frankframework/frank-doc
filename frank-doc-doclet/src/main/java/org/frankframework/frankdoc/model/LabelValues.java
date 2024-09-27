/*
Copyright 2022, 2024 WeAreFrank!

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LabelValues {
	private static final Logger log = LogUtil.getLogger(LabelValues.class);

	private final Map<String, List<String>> data = new HashMap<>();

	void setValues(String label, List<String> values) {
		data.put(label, values);
	}

	void addValue(String label, String value) {
		log.trace("Label [{}] can have value [{}]", label, value);
		data.putIfAbsent(label, new ArrayList<>());
		data.get(label).add(value);
	}

	List<String> getAllLabels() {
		List<String> allLabels = new ArrayList<>(data.keySet());
		Collections.sort(allLabels);
		return allLabels;
	}

	List<String> getAllValuesOfLabel(String label) {
		return data.get(label).stream()
				.distinct()
				.collect(Collectors.toList());
	}
}
