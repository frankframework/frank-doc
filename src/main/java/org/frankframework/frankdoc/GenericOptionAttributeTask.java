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
package org.frankframework.frankdoc;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.model.ElementRole;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.util.XmlBuilder;

import lombok.Getter;

class GenericOptionAttributeTask {
	private static Logger log = LogUtil.getLogger(GenericOptionAttributeTask.class);

	private final @Getter Set<ElementRole.Key> rolesKey;
	private @Getter String typeAttribute = "";
	private final @Getter XmlBuilder builder;

	GenericOptionAttributeTask(Set<ElementRole.Key> rolesKey, Collection<ElementRole> objectRoles, XmlBuilder builder) {
		this.rolesKey = rolesKey;
		this.builder = builder;
		List<String> typeAttributeCandidates = objectRoles.stream().map(ElementRole::getTypeAttribute).distinct().collect(Collectors.toList());
		if(typeAttributeCandidates.isEmpty()) {
			log.error("No typeAttribute candidate available for element roles [{}]", rolesKey.toString());
		} else if(typeAttributeCandidates.size() == 1) {
			typeAttribute = typeAttributeCandidates.get(0);
		} else {
			log.error("Ambiguous typeAttribute for element roles [{}], candidates are [{}]", rolesKey.toString(),
					typeAttributeCandidates.stream().collect(Collectors.toList()));
		}
	}
}
