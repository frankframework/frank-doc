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

import java.util.Map;

import org.frankframework.frankdoc.Constants;
import org.frankframework.frankdoc.FrankDocXsdFactory;
import org.frankframework.frankdoc.Utils;
import org.frankframework.frankdoc.wrapper.FrankMethod;

public class TextConfigChild extends ConfigChild {
	private static final Map<String, String> TEXT_ELEMENTS_WITH_SPECIAL_TYPES = Map.ofEntries(Map.entry(Constants.CONFIG_WARNING_ELEMENT_NAME, Constants.WARNING_ELEMENT_TYPE_NAME));
	private static final String TEXT_ELEMENT_DEFAULT_TYPE = FrankDocXsdFactory.ELEMENT_TYPE_STRING;

	private final String roleName;
	private final ConfigChildKey key;

	TextConfigChild(FrankElement owningElement, FrankMethod method, String roleName) {
		super(owningElement, method);
		this.roleName = roleName;
		this.key = new ConfigChildKey(roleName, null);
	}

	@Override
	public ConfigChildKey getKey() {
		return key;
	}

	// Avoid complicated Lombok syntax to add the @Override tag. Just
	// coding the getter is simpler in this case.
	@Override
	public String getRoleName() {
		return roleName;
	}

	public String getElementTypeName() {
		String elementName = Utils.toUpperCamelCase(roleName);
		if (TEXT_ELEMENTS_WITH_SPECIAL_TYPES.containsKey(elementName)) {
			return TEXT_ELEMENTS_WITH_SPECIAL_TYPES.get(elementName);
		}
		return TEXT_ELEMENT_DEFAULT_TYPE;
	}

	@Override
	public String toString() {
		return String.format("%s(roleName = %s)", getClass().getSimpleName(), roleName);
	}
}
