/*
Copyright 2020, 2021, 2022 WeAreFrank!

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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.DocWriterNew;
import org.frankframework.frankdoc.Utils;
import org.frankframework.frankdoc.util.LogUtil;

import java.util.function.Predicate;

/**
 * Base class of FrankAttribute and ConfigChild. This class was introduced
 * to implement the following common logic:
 * <ul>
 * <li> The decision whether to include an attribute or config child in the XML schema
 * is based on the same information.
 * <li> The structure is very similar in the XML schema for config children and
 * attributes. In both cases, we have cumulative groups that include inherited
 * items and declared groups that hold only items at the present level of the
 * inheritance hierarchy. Please see this in action at {@link DocWriterNew}.
 * </ul>
 *
 * @author martijn
 */
public abstract class ElementChild {
	private static final Logger log = LogUtil.getLogger(ElementChild.class);

	private final @Getter FrankElement owningElement;

	/**
	 * The value is inherited from ElementChild corresponding to superclass.
	 */
	private @Getter @Setter DeprecationInfo deprecationInfo;

	/**
	 * This field supports the PROTECTED feature, which causes an attribute or config child to be excluded.
	 * Also suppresses inheritance.
	 */
	private @Getter @Setter boolean excluded = false;

	private @Getter @Setter(AccessLevel.PACKAGE) MandatoryStatus mandatoryStatus = MandatoryStatus.OPTIONAL;

	/**
	 * Only set to true if there is an IbisDoc or IbisDocRef annotation for
	 * this specific ElementChild, excluding inheritance. This property is
	 * intended to detect Java Override annotations that are only there for
	 * technical reasons, without relevance to the Frank developer.
	 *
	 * But values inside IbisDoc or IbisDocRef annotations are inherited.
	 * That is the case to allow documentation information to be stored more
	 * centrally.
	 */
	private @Getter @Setter boolean documented;
	private @Getter FrankElement overriddenFrom;

	private @Getter @Setter boolean reintroduced = false;

	/**
	 * This property is used to omit "technical overrides" from the XSDs. Sometimes
	 * the Java code of the F!F overrides a method without a change of meaning of
	 * the corresponding attribute or config child. Such technical overrides should
	 * not be used to add attributes or config children to the XSDs.
	 * <p>
	 * The property is a bit different for attributes and config children, but we
	 * define it here because it is used the same way for both attributes and
	 * config children.
	 */
	private @Getter @Setter boolean technicalOverride = false;
	private boolean isOverrideMeaningfulLogged = false;

	/**
	 * Different {@link ElementChild} of the same FrankElement are allowed to have the same order.
	 */
	private @Getter(AccessLevel.PACKAGE) @Setter(AccessLevel.PACKAGE) int order = Integer.MAX_VALUE;
	private @Getter @Setter String description;
	private @Getter @Setter String defaultValue;

	public static Predicate<ElementChild> IN_XSD = c ->
		(! c.isExcluded())
		&& (! c.isDeprecated())
		&& (c.isDocumented() || (! c.isTechnicalOverride()));

	public static Predicate<ElementChild> IN_COMPATIBILITY_XSD = c ->
		(! c.isExcluded())
		&& (c.isDocumented() || (! c.isTechnicalOverride()));

	public static Predicate<ElementChild> REJECT_DEPRECATED = c -> c.isExcluded() || c.isDeprecated();
	static Predicate<ElementChild> ALL = c -> true;
	public static Predicate<ElementChild> ALL_NOT_EXCLUDED = c -> ! c.isExcluded();
	public static Predicate<ElementChild> EXCLUDED = ElementChild::isExcluded;
	public static Predicate<ElementChild> JSON_NOT_INHERITED = c -> c.isExcluded() && (c.getOverriddenFrom() != null);
	// A config child is also relevant for the JSON if it is excluded. The frontend has to mention it as not inherited.
	// Technical overrides are not relevant. But isTechnicalOverride() is also true for undocumented
	// excluded children. Of course we have to include those.
	public static Predicate<ElementChild> JSON_RELEVANT = IN_COMPATIBILITY_XSD.or(JSON_NOT_INHERITED);

	/**
	 * Base class for keys used to look up {@link FrankAttribute} objects or
	 * {@link ConfigChild} objects from a map.
	 */
	abstract static class AbstractKey {
	}

	ElementChild(final FrankElement owningElement) {
		this.owningElement = owningElement;
	}

	void clearDefaultValue() {
		defaultValue = null;
	}

	void calculateOverriddenFrom() {
		FrankElement match = getOwningElement();
		while(match.getParent() != null) {
			match = match.getParent();
			ElementChild matchingChild = match.findElementChildMatch(this);
			if(matchingChild != null) {
				if(matchingChild.isDeprecated()) {
					log.warn("Element child overrides deprecated ElementChild: descendant [{}], super [{}]", this::toString, matchingChild::toString);
				}
				overriddenFrom = match;
				if(! overrideIsMeaningful(matchingChild)) {
					technicalOverride = true;
				}
				log.trace("{} [{}] of FrankElement [{}] has overriddenFrom = [{}]",
						() -> getClass().getSimpleName(), this::toString, owningElement::getFullName, () -> overriddenFrom.getFullName());
				return;
			}
		}
	}

	final boolean overrideIsMeaningful(ElementChild overriddenFrom) {
		// Do not test whether property "deprecated" has changed. If an overridden method is deprecated
		// and the overriding method is not deprecated, then the override can have no annotations and no
		// JavaDoc. Then it is still a technical override.
		//
		// There are corner cases in which a technical override is not recognized, which
		// causes an attribute to be reintroduced for a derived class. For example,
		// if an attribute is MANDATORY in a parent class and BECOMES_MANDATORY in a
		// derived class, then this is no reason to reintroduce it in FrankConfig-strict.xsd.
		// Reintroducing is not an error however. It only makes the XSDs larger than is
		// strictly necessary. We accept this behavior to avoid code complexity. In the
		// mentioned example, we have a meaningful override for FrankConfig-compatibility.xsd.
		// We do not want to make overrideIsMeaningful() dependent on the XsdVersion, because
		// that is not part of the model.
		boolean result = reintroduced
				|| (getMandatoryStatus() != overriddenFrom.getMandatoryStatus())
				|| (isExcluded() != overriddenFrom.isExcluded())
				|| (! Utils.equalsNullable(getDescription(), overriddenFrom.getDescription()))
				|| (! Utils.equalsNullable(getDefaultValue(), overriddenFrom.getDefaultValue()));
		result = result || specificOverrideIsMeaningful(overriddenFrom);
		if(log.isTraceEnabled() && (! isOverrideMeaningfulLogged) && result) {
			isOverrideMeaningfulLogged = true;
			log.trace("ElementChild {} overrides {} and changes something relevant for the Frank!Doc", toString(), overriddenFrom.toString());
		}
		return result;
	}

	abstract boolean specificOverrideIsMeaningful(ElementChild overriddenFrom);

	@Override
	public String toString() {
		return String.format("(Key %s, owner %s)", getKey().toString(), owningElement.getFullName());
	}

	/**
	 * Get key that is used to match overrides. If {@link FrankElement} <code>A</code>
	 * is a descendant of {@link FrankElement} <code>B</code> and if their
	 * respective attributes <code>a</code> and <code>b</code> have an equal key,
	 * then attribute <code>a</code> is assumed to override attribute <code>b</b>.
	 * This function has the same purpose for config children.
	 */
	abstract AbstractKey getKey();

	public boolean isDeprecated() {
		return this.deprecationInfo != null;
	}

}
