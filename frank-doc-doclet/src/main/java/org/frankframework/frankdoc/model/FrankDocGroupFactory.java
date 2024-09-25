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

import lombok.NonNull;
import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankEnumConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;

class FrankDocGroupFactory {
	static final String JAVADOC_GROUP_ANNOTATION = "org.frankframework.doc.FrankDocGroup";
	static final String FRANK_DOC_GROUP_VALUES_CLASS = FRANK_DOC_GROUP_VALUES_PACKAGE + "FrankDocGroupValue";
	private static final Logger log = LogUtil.getLogger(FrankDocGroupFactory.class);

	private final Map<String, Integer> groupValuesWithPositions = new HashMap<>();
	private final Map<String, FrankDocGroup> groups = new HashMap<>();

	// This constructor ensures that group Other is always created, also if there
	// is no ElementType without a FrankDocGroup annotation. We always need group
	// Other because it will contain Configuration and Module.
	FrankDocGroupFactory(@NonNull FrankClassRepository classes) {
		try {
			FrankClass valuesEnum = classes.findClass(FRANK_DOC_GROUP_VALUES_CLASS);
			if(valuesEnum == null) {
				log.error("Class [{}] has not been loaded", FRANK_DOC_GROUP_VALUES_CLASS);
			}
			int position = 0;
			for(FrankEnumConstant enumConstant: valuesEnum.getEnumConstants()) {
				groupValuesWithPositions.put(enumConstant.getName(), position++);
			}
		} catch(FrankDocException e) {
			log.error("Cannot find value class {} for @FrankDocGroup", FRANK_DOC_GROUP_VALUES_CLASS);
		}
		FrankDocGroup groupOther = new FrankDocGroup(FrankDocGroup.GROUP_NAME_OTHER);
		groups.put(groupOther.getName(), groupOther);
	}

	FrankDocGroup findOrCreateGroup(FrankClass clazz) {
		try {
			FrankAnnotation annotation = clazz.getAnnotationIncludingInherited(JAVADOC_GROUP_ANNOTATION);
			if (annotation == null) {
				log.trace("Class [{}] belongs to group [{}]", () -> clazz.getName(), () -> FrankDocGroup.GROUP_NAME_OTHER);
				return groups.get(FrankDocGroup.GROUP_NAME_OTHER);
			}

			FrankEnumConstant enumConstant = (FrankEnumConstant) annotation.getValue();
			String groupName = new EnumValue(enumConstant).getLabel();
			if (groups.containsKey(groupName)) {
				FrankDocGroup group = groups.get(groupName);
				log.trace("Class [{}] belongs to found group [{}]", () -> clazz.getName(), () -> group.getName());
				return group;
			}

			int groupOrder = Optional.of(groupValuesWithPositions.get(enumConstant.getName())).orElseThrow(
				() -> new FrankDocException("Programming error: Could not get position of enum constant " + enumConstant.getName(), null));
			FrankDocGroup group = new FrankDocGroup(groupName);
			group.setOrder(groupOrder);
			log.trace("Class [{}] belongs to new group [{}], order is [{}]", () -> clazz.getName(), () -> group.getName(), () -> group.getOrder());
			groups.put(groupName, group);
			return group;
		} catch(FrankDocException e) {
			log.error("Class [{}] has invalid @FrankDocGroup: {}", clazz.getName(), e.getMessage());
			return groups.get(FrankDocGroup.GROUP_NAME_OTHER);
		}
	}

	List<FrankDocGroup> getAllGroups() {
		List<FrankDocGroup> result = new ArrayList<>(groups.values());
		Collections.sort(result);
		return result;
	}
}
