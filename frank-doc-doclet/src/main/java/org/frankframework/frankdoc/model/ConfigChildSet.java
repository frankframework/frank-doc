/*
Copyright 2021, 2025 WeAreFrank!

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;

import lombok.Getter;

/**
 * Holds the list of all cumulative config children sharing some role name, say R, but
 * only if there is no ancestor having the same config children for role name R.
 * <p>
 * Example. Assume that a class Super has a derived class Derived. Super has a method
 * registerChild(A a) while Derived has a method registerChild(B b). Then the cumulative
 * config children of Derived with role name "child" are (A, child), (B, child). These
 * two go into a ConfigChildSet with common role name "child".
 * <p>
 * If the method registerChild(B b) would not be in Derived but in Super, then the cumulative
 * config children with role name "child" would be the same for Derived and Super. Therefore
 * only Super would have a ConfigChildSet for this role name, not Derived.
 *
 * @author martijn
 */
public class ConfigChildSet {
	private static final Logger log = LogUtil.getLogger(ConfigChildSet.class);

	private final @Getter List<ConfigChild> configChildren;

	/**
	 * @throws IllegalStateException if input children is empty or when the elements
	 * are not in the right sort order
	 * or do not share a role name. The children should be sorted from derived to ancestor
	 * owning element and then by order. This precondition should be enforced
	 * by {@link AncestorChildNavigation}.
	 */
	ConfigChildSet(List<ConfigChild> configChildren) {
		this.configChildren = configChildren;
		if (configChildren.isEmpty()) {
			throw new IllegalStateException("A config child cannot have an empty list of config childs");
		}
		if (configChildren.size() >= 2) {
			FrankElement owner = configChildren.get(0).getOwningElement();
			ConfigChild previous = configChildren.get(0);
			boolean sameOwner;
			for (ConfigChild c : configChildren.subList(1, configChildren.size())) {
				sameOwner = true;
				// We have a small selection of the config children here that corresponds to a shared role name.
				// When these are sorted, the owning elements can skip owning elements in the inheritance hierarchy.
				while (c.getOwningElement() != owner) {
					if (owner == null) {
						throw new IllegalStateException(String.format("Cumulative config children are not sorted by owning elements and their ancestor hierarchy: [%s] should not be followed by [%s]",
							previous.toString(), c));
					}
					owner = owner.getParent();
					sameOwner = false;
				}
				if (sameOwner && previous.getOrder() > c.getOrder()) {
					throw new IllegalStateException(String.format("Cumulative config children are not sorted by order. Offending config child [%s]",
						c.getKey().toString()));
				}
				previous = c;
			}
		}
	}

	public ConfigChildGroupKind getConfigChildGroupKind() {
		return ConfigChildGroupKind.groupKind(configChildren);
	}

	public Stream<ElementRole> getElementRoleStream() {
		return ConfigChild.getElementRoleStream(configChildren);
	}

	public String getRoleName() {
		return configChildren.get(0).getRoleName();
	}

	public List<ElementRole> getFilteredElementRoles(Predicate<ElementChild> selector, Predicate<ElementChild> rejector) {
		List<ConfigChild> filteredConfigChildren = filter(selector, rejector);
		return ConfigChild.getElementRoleStream(filteredConfigChildren).toList();
	}

	List<ConfigChild> filter(Predicate<ElementChild> selector, Predicate<ElementChild> rejector) {
		Set<ConfigChildKey> keys = configChildren.stream().map(ConfigChild::getKey).collect(Collectors.toSet());
		List<ConfigChild> result = new ArrayList<>();
		for (ConfigChild c : configChildren) {
			if (rejector.test(c)) {
				keys.remove(c.getKey());
			} else if (selector.test(c)) {
				result.add(c);
				keys.remove(c.getKey());
			}
		}
		return result;
	}

	/**
	 * Handles generic element option recursion as explained in
	 * {@link org.frankframework.frankdoc.model}.
	 */
	public static Map<String, List<ConfigChild>> getMemberChildren(
		List<ElementRole> parents, Predicate<ElementChild> selector, Predicate<ElementChild> rejector, Predicate<FrankElement> elementFilter) {
		if (log.isTraceEnabled()) {
			log.trace("ConfigChildSet.getMemberChildren called with parents: [{}]", elementRolesToString(parents));
		}
		List<FrankElement> members = parents.stream()
			.flatMap(role -> role.getMembers().stream())
			.filter(elementFilter)
			.distinct()
			.toList();
		if (log.isTraceEnabled()) {
			String elementsString = members.stream().map(FrankElement::getSimpleName).collect(Collectors.joining(", "));
			log.trace("Members of parents are: [{}]", elementsString);
		}
		Map<String, List<ConfigChild>> memberChildrenByRoleName = members.stream().flatMap(element -> element.getCumulativeConfigChildren(selector, rejector).stream())
			.distinct()
			.collect(Collectors.groupingBy(ConfigChild::getRoleName));
		if (log.isTraceEnabled()) {
			log.trace("Found the following member children:");
			for (var entry : memberChildrenByRoleName.entrySet()) {
				String roleName = entry.getKey();
				List<ConfigChild> memberChildren = entry.getValue();
				String memberChildrenString = memberChildren.stream()
					.map(ConfigChild::toString)
					.collect(Collectors.joining(", "));
				log.trace("  [{}]: [{}]", roleName, memberChildrenString);
			}
		}
		return memberChildrenByRoleName;
	}

	private static String elementRolesToString(List<ElementRole> elementRoles) {
		return elementRoles.stream().map(ElementRole::toString).collect(Collectors.joining(", "));
	}

	public static Set<ElementRole.Key> getKey(List<ElementRole> roles) {
		return roles.stream().map(ElementRole::getKey).collect(Collectors.toSet());
	}

	public Optional<String> getGenericElementOptionDefault(Predicate<FrankElement> elementFilter) {
		List<String> candidates = ConfigChild.getElementRoleStream(configChildren)
			.flatMap(ConfigChildSet::getCandidatesForGenericElementOptionDefault)
			.toList();
		if (candidates.size() == 1) {
			return Optional.of(candidates.get(0));
		} else {
			if (candidates.size() >= 2) {
				if (configChildren.stream()
					.map(ConfigChild::getRoleName)
					.anyMatch(roleName -> roleName.equals("child"))) {
					// We cannot fix this for Frank config element <Child>, so the build should not fail.
					log.warn("ConfigChildSet [{}] has multiple candidates for the default element: [{}]", this,
						String.join(", ", candidates));
				} else {
					// We are hiding FrankElement-s. For the sake of the argument, say class Pipe implements interface IPipe,
					// and say we have a config child with role name "pipe".
					// Then <Pipe className="..."> should work, syntax 1. But <Pipe> without className should
					// reference class Pipe. We implement this by having a default value for the className attribute.
					// But that does not work if there are multiple candidates.
					log.error("ConfigChildSet [{}] has multiple candidates for the default element: [{}]", this,
						String.join(", ", candidates));
				}
			}
			return Optional.empty();
		}
	}

	private static Stream<String> getCandidatesForGenericElementOptionDefault(ElementRole e) {
		List<String> result = new ArrayList<>();
		String forJavaDoc = e.getDefaultElement();
		if (forJavaDoc != null) {
			result.add(forJavaDoc);
		}
		FrankElement forConflictElement = e.getDefaultElementOptionConflict();
		Optional.ofNullable(forConflictElement).map(FrankElement::getFullName).ifPresent(result::add);
		return result.stream();
	}

	@Override
	public String toString() {
		return "ConfigChildSet(" + configChildren.stream().map(ConfigChild::toString).collect(Collectors.joining(", ")) + ")";
	}
}
