/*
Copyright 2021, 2022 WeAreFrank!

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
import lombok.Setter;
import org.frankframework.frankdoc.wrapper.FrankMethod;

public class ObjectConfigChild extends ConfigChild {
	private @Getter @Setter ElementRole elementRole;

	ObjectConfigChild(FrankElement owningElement, FrankMethod method) {
		super(owningElement, method);
	}

	@Override
	public ConfigChildKey getKey() {
		return new ConfigChildKey(getRoleName(), elementRole.getElementType());
	}

	@Override
	public String getRoleName() {
		if (elementRole == null)
			return null;
		return elementRole.getRoleName();
	}

	public ElementType getElementType() {
		return elementRole.getElementType();
	}

	@Override
	public String toString() {
		String elementTypeName = "<under construction>";
		if(elementRole != null) {
			elementTypeName = getElementType().getSimpleName();
		}
		return String.format("%s(%s.%s(%s))",
				this.getClass().getSimpleName(), getOwningElement().getSimpleName(), getMethodName(), elementTypeName);
	}
}
