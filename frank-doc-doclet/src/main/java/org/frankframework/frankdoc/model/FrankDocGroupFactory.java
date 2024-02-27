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

import org.frankframework.frankdoc.model.FrankDocGroup;

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankEnumConstant;

import java.util.*;

class FrankDocGroupFactory {
	static final String JAVADOC_GROUP_ANNOTATION = "org.frankframework.doc.FrankDocGroup";

	private static Logger log = LogUtil.getLogger(FrankDocGroupFactory.class);

	private final Map<String, FrankDocGroup> allGroups = new HashMap<>();

	// This constructor ensures that group Other is always created, also if there
	// is no ElementType without a FrankDocGroup annotation. We always need group
	// Other because it will contain Configuration and Module.
	FrankDocGroupFactory() {
	}

	FrankDocGroup getGroup(FrankClass clazz) {
		try {
			FrankAnnotation annotation = clazz.getAnnotationIncludingInherited(JAVADOC_GROUP_ANNOTATION);
			if(annotation == null) {
				if(! allGroups.containsKey(FrankDocGroup.GROUP_NAME_OTHER)) {
					FrankDocGroup groupOther = new FrankDocGroup(FrankDocGroup.GROUP_NAME_OTHER);
					allGroups.put(groupOther.getName(), groupOther);
				}
				log.trace("Class [{}] belongs to group [{}]", () -> clazz.getName(), () -> FrankDocGroup.GROUP_NAME_OTHER);
				return allGroups.get(FrankDocGroup.GROUP_NAME_OTHER);
			} else {
				FrankEnumConstant enumConstant = (FrankEnumConstant) annotation.getValue();
				String groupName = new EnumValue(enumConstant).getLabel();
				if(allGroups.containsKey(groupName)) {
					FrankDocGroup group = allGroups.get(groupName);
					log.trace("Class [{}] belongs to found group [{}]", () -> clazz.getName(), () -> group.getName());
					return group;
				} else {
					int groupOrder = enumConstant.getPosition();
					FrankDocGroup group = new FrankDocGroup(groupName);
					group.setOrder(groupOrder);
					log.trace("Class [{}] belongs to new group [{}], order is [{}]", () -> clazz.getName(), () -> group.getName(), () -> group.getOrder());
					allGroups.put(groupName, group);
					return group;
				}
			}
		} catch(FrankDocException e) {
			log.error("Class [{}] has invalid @FrankDocGroup: {}", clazz.getName(), e.getMessage());
			return allGroups.get(FrankDocGroup.GROUP_NAME_OTHER);
		}
	}

	List<FrankDocGroup> getAllGroups() {
		List<FrankDocGroup> result = new ArrayList<>(allGroups.values());
		Collections.sort(result);
		return result;
	}
}
