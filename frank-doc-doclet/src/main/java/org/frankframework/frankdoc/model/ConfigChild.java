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
import org.frankframework.frankdoc.XsdVersion;
import org.frankframework.frankdoc.feature.Deprecated;
import org.frankframework.frankdoc.feature.Description;
import org.frankframework.frankdoc.feature.Reintroduce;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A method on a class, which indicates which child elements can be used.
 * For example for a Configuration: registerInclude, registerAdapter, setSapSystems and setMonitoring.
 *
 * <p>
 * All methods with <code>set</code> are singular and can only be used once within the current element.
 * All methods with <code>register</code> are plural and can be used multiple times within the current element.
 * </p>
 */
public abstract class ConfigChild extends ElementChild {
	private static Logger log = LogUtil.getLogger(ConfigChild.class);

	private static final Comparator<ConfigChild> SINGLE_ELEMENT_ONLY =
			Comparator.comparing(c -> ! c.isAllowMultiple());

	private static final Comparator<ConfigChild> REMOVE_DUPLICATES_COMPARATOR =
			SINGLE_ELEMENT_ONLY.thenComparing(ElementChild::getMandatoryStatus);

	private @Getter @Setter boolean allowMultiple;
	private @Getter(AccessLevel.PACKAGE) String methodName;

	ConfigChild(FrankElement owningElement, FrankMethod method) {
		super(owningElement);
		setDocumented(Description.getInstance().valueOf(method) != null);
		setDeprecationInfo(Deprecated.getInstance().getInfo(method));
		setReintroduced(Reintroduce.getInstance().isSetOn(method));
		log.trace("ConfigChild of method {} has documented={}, deprecated={}, reintroduced={}", () -> method.toString(), () -> isDocumented(), () -> isDeprecated(), () -> isReintroduced());
		this.methodName = method.getName();
	}

	public abstract String getRoleName();

	@Override
	abstract ConfigChildKey getKey();

	/**
	 * Removes duplicate config children. As input this method expects a list of config children
	 * found from the declared methods of a Java class. The isMandatory() and isAllowMultiple()
	 * properties of a config child follow from the name of the Java method. Therefore, the
	 * input config children are unique by the combination of role name, ElementType,
	 * isAllowMultiple() and isMandatory().
	 * <p>
	 * Now consider Java class <code>org.frankframework.senders.SenderSeries</code>. It both has methods
	 * setSender() and registerSender(), which would cause a duplicate config child. If both would be
	 * included, the XSDs would define multiple times that a SenderSeries can have a sender as child.
	 * This method makes the config children unique by role name and element type, which means
	 * unique by {@link org.frankframework.frankdoc.model.ElementRole}.
	 * <p>
	 * The fix implemented by this method only works if a config child was allowed only once and if
	 * it is updated to being allowed multiple times. The reverse change won't work. With that change,
	 * there would be a duplicate config child. The first would be a deprecated config child that
	 * is allowed multiple times. The second would be a non-deprecated config child that is allowed only once.
	 * This method would then select the config child that is allowed only once, because that would be
	 * the first after the sort applied in this method. But that config child setter is deprecated and it
	 * would be the only one left for the {@link ElementRole}. Therefore, strict.xsd would no longer
	 * have a config child for the {@link ElementRole}.
	 */
	static List<ConfigChild> removeDuplicates(List<ConfigChild> orig) {
		Map<ConfigChildKey, List<ConfigChild>> byKey = orig.stream().collect(Collectors.groupingBy(ConfigChild::getKey));
		List<ConfigChild> result = new ArrayList<>();
		for(ConfigChildKey key: byKey.keySet()) {
			List<ConfigChild> bucket = new ArrayList<>(byKey.get(key));
			Collections.sort(bucket, REMOVE_DUPLICATES_COMPARATOR);
			ConfigChild selected = bucket.get(0);
			result.add(selected);
			if(selected.isDeprecated()) {
				if(bucket.stream().allMatch(ElementChild::isDeprecated)) {
					log.trace("All config children with key [{}] are deprecated", () -> key.toString());
				} else {
					log.error("From duplicate config children, only a deprecated one is selected. In mode {}, {} will not have a config child for {}",
							() -> XsdVersion.STRICT, () -> selected.getOwningElement().getFullName(), () -> selected.toString());
				}
			}
			if(log.isTraceEnabled() && (bucket.size() >= 2)) {
				for(ConfigChild omitted: bucket.subList(1, bucket.size())) {
					log.trace("Omitting config child {} because it is a duplicate of {}", omitted.toString(), selected.toString());
				}
			}
		}
		return result;
	}

	@Override
	boolean specificOverrideIsMeaningful(ElementChild overriddenFrom) {
		ConfigChild match = (ConfigChild) overriddenFrom;
		return (allowMultiple != match.allowMultiple);
	}

	public static Stream<ElementRole> getElementRoleStream(Collection<ConfigChild> configChildren) {
		return configChildren.stream()
				.filter(c -> c instanceof ObjectConfigChild)
				.map(c -> (ObjectConfigChild) c)
				.map(ObjectConfigChild::getElementRole)
				.distinct();
	}

	public static String toString(Collection<ConfigChild> configChildren) {
		return configChildren.stream().map(ConfigChild::toString).collect(Collectors.joining(", "));
	}
}
