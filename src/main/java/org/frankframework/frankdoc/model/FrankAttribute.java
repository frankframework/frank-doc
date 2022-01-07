/* 
Copyright 2020, 2021 WeAreFrank! 

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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankType;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

public class FrankAttribute extends ElementChild {
	private static Logger log = LogUtil.getLogger(FrankAttribute.class);

	static final String JAVADOC_ATTRIBUTE_REF = "@ff.ref";

	@EqualsAndHashCode(callSuper = false)
	static class Key extends AbstractKey {
		private String name;

		Key(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private @Getter String name;
	private @Getter @Setter FrankElement describingElement;

	private @Getter @Setter AttributeType attributeType;

	/**
	 * Null if there is no restriction to the allowed attribute values. Should only be set if attributeType == {@link AttributeType#STRING}.
	 */
	private @Getter @Setter AttributeEnum attributeEnum;

	/**
	 * This field supports the ff.noAttribute and ff.ignoreTypeMembership JavaDoc tags. 
	 * These annotations should cause attributes to not exist. If an attribute should
	 * not exist, then it also should not be inherited.
	 */
	private @Getter @Setter(AccessLevel.PACKAGE) boolean excluded = false;
	private @Getter @Setter(AccessLevel.PACKAGE) boolean mandatory = false;

	public FrankAttribute(String name, FrankElement attributeOwner) {
		super(attributeOwner);
		this.name = name;
		this.describingElement = attributeOwner;
	}

	@Override
	public Key getKey() {
		return new Key(name);
	}

	@Override
	boolean overrideIsMeaningful(ElementChild overriddenFrom) {
		return false;
	}

	/**
	 * If an explicit default value of null is given for an object type attribute, then
	 * it means that it is allowed not to set the attribute. This means we do not want a
	 * default value in the XSDs. We implement this by omitting the default value.
	 */
	void handleDefaultExplicitNull(FrankType parameterType) {
		if(getDefaultValue() == null) {
			return;
		}
		if(mandatory) {
			log.warn("Attribute [{}] is mandatory, but it also has a default value: [{}]", toString(), getDefaultValue());
		}
		boolean isExplicitNull = (StringUtils.isBlank(getDefaultValue()) || getDefaultValue().equals("null"));
		if(isExplicitNull && parameterType.isPrimitive()) {
			log.error("Attribute [{}] is of primitive type [{}] but has default value null", toString(), parameterType.toString());
			return;
		}
		if(isExplicitNull) {
			log.trace("Attribute [{}] has explicit default value [{}], clearing the default value", () -> toString(), () -> getDefaultValue());
			clearDefaultValue();
		}
	}

	void typeCheckDefaultValue() throws FrankDocException {
		if(getDefaultValue() != null) {
			attributeType.typeCheck(getDefaultValue());
			if((attributeType == AttributeType.STRING) && (attributeEnum != null)) {
				attributeEnum.typeCheck(getDefaultValue());
			}
		}
	}

	@Override
	public String toString() {
		return String.format("%s.%s", getOwningElement().getSimpleName(), name);
	}
}
