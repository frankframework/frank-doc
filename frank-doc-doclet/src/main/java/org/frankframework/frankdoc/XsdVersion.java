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
package org.frankframework.frankdoc;

import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.addChoice;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.addSequence;

import java.util.function.Predicate;

import org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.AttributeUse;
import org.frankframework.frankdoc.model.ConfigChild;
import org.frankframework.frankdoc.model.ElementChild;
import org.frankframework.frankdoc.model.FrankAttribute;
import org.frankframework.frankdoc.model.FrankElement;
import org.frankframework.frankdoc.model.MandatoryStatus;
import org.frankframework.frankdoc.util.XmlBuilder;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public enum XsdVersion {
	STRICT(ElementChild.IN_XSD, ElementChild.REJECT_DEPRECATED, f -> ! f.isDeprecated(), new DelegateStrict(), "Public FrankDoc XSD, should be used within an IDE to validate a configuration."),
	COMPATIBILITY(ElementChild.IN_COMPATIBILITY_XSD, ElementChild.EXCLUDED, f -> true, new DelegateCompatibility(), "Compatibility XSD, internal use only!");

	private final @Getter Predicate<ElementChild> childSelector;
	private final @Getter Predicate<ElementChild> childRejector;
	private final @Getter Predicate<FrankElement> elementFilter;
	private final Delegate delegate;
	private final @Getter String description;

	private XsdVersion(Predicate<ElementChild> childSelector, Predicate<ElementChild> childRejector, Predicate<FrankElement> elementFilter, Delegate delegate, String description) {
		this.childSelector = childSelector;
		this.childRejector = childRejector;
		this.elementFilter = elementFilter;
		this.delegate = delegate;
		this.description = description;
	}

	Predicate<FrankElement> getHasRelevantChildrenPredicate(Class<? extends ElementChild> kind) {
		return f -> ! f.getChildrenOfKind(childSelector.or(childRejector), kind).isEmpty();
	}

	void checkForMissingDescription(FrankAttribute attribute) {
		delegate.checkForMissingDescription(attribute);
	}

	void checkForMissingDescription(ConfigChild configChild) {
		delegate.checkForMissingDescription(configChild);
	}

	AttributeUse getRoleNameAttributeUse() {
		return delegate.getRoleNameAttributeUse();
	}

	AttributeUse getClassNameAttributeUse(FrankElement frankElement) {
		return delegate.getClassNameAttributeUse(frankElement);
	}

	XmlBuilder configChildBuilderWithinSequence(XmlBuilder context) {
		return delegate.configChildBuilderWithinSequence(context);
	}

	XmlBuilder configChildBuilder(XmlBuilder context) {
		return delegate.configChildBuilder(context);
	}

	boolean childIsMandatory(ElementChild child) {
		return delegate.childIsMandatory(child);
	}

	private abstract static class Delegate {
		abstract void checkForMissingDescription(FrankAttribute attribute);
		abstract void checkForMissingDescription(ConfigChild configChild);
		abstract AttributeUse getRoleNameAttributeUse();
		abstract AttributeUse getClassNameAttributeUse(FrankElement frankElement);
		abstract XmlBuilder configChildBuilderWithinSequence(XmlBuilder context);
		abstract XmlBuilder configChildBuilder(XmlBuilder context);
		abstract boolean childIsMandatory(ElementChild child);
	}

	private static class DelegateStrict extends Delegate {
		@Override
		void checkForMissingDescription(FrankAttribute attribute) {
			if(attribute.getDescription() != null) {
				return;
			}
			log.info("Attribute [{}] lacks description", attribute);
		}

		@Override
		void checkForMissingDescription(ConfigChild configChild) {
			if(configChild.getDescription() != null) {
				return;
			}
			log.info("Config child [{}] lacks description", configChild);
		}

		AttributeUse getRoleNameAttributeUse() {
			return AttributeUse.PROHIBITED;
		}

		@Override
		AttributeUse getClassNameAttributeUse(FrankElement frankElement) {
			return AttributeUse.PROHIBITED;
		}

		@Override
		XmlBuilder configChildBuilderWithinSequence(XmlBuilder context) {
			return context;
		}

		@Override
		XmlBuilder configChildBuilder(XmlBuilder context) {
			return context;
		}

		@Override
		boolean childIsMandatory(ElementChild child) {
			return child.getMandatoryStatus() != MandatoryStatus.OPTIONAL;
		}
	}

	private static class DelegateCompatibility extends Delegate {
		@Override
		void checkForMissingDescription(FrankAttribute attribute) {
			// No-op in this implementation
		}

		@Override
		void checkForMissingDescription(ConfigChild configChild) {
			// No-op in this implementation
		}

		/**
		 * Fix of https://github.com/ibissource/iaf/issues/1760. We need
		 * to omit the "use" attribute of the "elementRole" attribute in
		 * the compatibility XSD.
		 */
		@Override
		AttributeUse getRoleNameAttributeUse() {
			return AttributeUse.OPTIONAL;
		}

		/**
		 * Fix of https://github.com/ibissource/iaf/issues/1760. We need
		 * to omit the "use" attribute of the "className" attribute in
		 * the compatibility XSD, but only for interface-based FrankElement-s.
		 */
		@Override
		AttributeUse getClassNameAttributeUse(FrankElement frankElement) {
			if(frankElement.isInterfaceBased()) {
				return AttributeUse.OPTIONAL;
			} else {
				return AttributeUse.PROHIBITED;
			}
		}

		@Override
		XmlBuilder configChildBuilderWithinSequence(XmlBuilder context) {
			return addChoice(context, "0", "unbounded");
		}

		@Override
		XmlBuilder configChildBuilder(XmlBuilder context) {
			XmlBuilder sequence = addSequence(context);
			return addChoice(sequence, "0", "unbounded");
		}

		@Override
		boolean childIsMandatory(ElementChild child) {
			return child.getMandatoryStatus() == MandatoryStatus.MANDATORY;
		}
	}
}
