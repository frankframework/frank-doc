/*
Copyright 2022 WeAreFrank!

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

	private abstract static class SortableValue implements Comparable<SortableValue> {
		private @Getter final String value;

		SortableValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	private static final class EnumValue extends SortableValue {
		private final int order;

		EnumValue(String value, int order) {
			super(value);
			this.order = order;
		}

		@Override
		public int compareTo(SortableValue other) {
			// It is a programming error if a label has both enum values and
			// non-enum values. If this happens, a ClassCastException will occur.
			return Integer.compare(order, ((EnumValue) other).order);
		}
	}

	private static final class SimpleValue extends SortableValue {
		SimpleValue(String value) {
			super(value);
		}

		@Override
		public int compareTo(SortableValue other) {
			// It is a programming error if a label has both enum values and
			// non-enum values. If this happens, a ClassCastException will occur.
			return getValue().compareTo(((SimpleValue) other).getValue());
		}
	}

	private boolean isInitialized = false;
	private final Map<String, List<SortableValue>> data = new HashMap<>();

	void addValue(String label, String value) {
		log.trace("Label [{}] can have non-enum value [{}]", label, value);
		add(label, new SimpleValue(value));
	}

	void addEnumValue(String label, String value, int order) {
		log.trace("Label [{}] can have enum value [{}], position is [{}]", label, value, order);
		add(label, new EnumValue(value, order));
	}

	private void add(String label, SortableValue value) {
		data.putIfAbsent(label, new ArrayList<>());
		data.get(label).add(value);
	}

	void finishInitialization() {
		for(String label: data.keySet()) {
			Collections.sort(data.get(label));
		}
		isInitialized = true;
	}

	List<String> getAllLabels() {
		checkInitialized();
		List<String> allLabels = new ArrayList<>(data.keySet());
		Collections.sort(allLabels);
		return allLabels;
	}

	private void checkInitialized() {
		if(! isInitialized) {
			// No more information needed. If this exception occurs it is a programming
			// error. The stack trace will be sufficient information.
			throw new IllegalStateException("Cannot provide label values before finishing initialization");
		}
	}
	List<String> getAllValuesOfLabel(String label) {
		checkInitialized();
		return data.get(label).stream()
				.map(SortableValue::getValue)
				.distinct()
				.collect(Collectors.toList());
	}
}
