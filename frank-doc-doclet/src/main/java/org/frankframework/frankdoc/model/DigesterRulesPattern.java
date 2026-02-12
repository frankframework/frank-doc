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

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class DigesterRulesPattern {
	private final String originalPattern;
	private final List<String> components;
	private @Getter Matcher matcher;

	DigesterRulesPattern(String pattern) throws SAXException {
		this.originalPattern = pattern;
		boolean matchesOnlyRoot = false;
		if(StringUtils.isBlank(pattern)) {
			throw new SAXException("digester-rules.xml: Pattern cannot be null and cannot be blank");
		}
		components = Arrays.asList(pattern.split("/"));
		if(components.isEmpty()) {
			throw new SAXException(String.format("digester-rules.xml should not contain an empty pattern. The literal value was [%s]", originalPattern));
		}
		List<String> componentsThatShouldNotBeWildcard = components;
		if (components.getFirst().equals("*")) {
			componentsThatShouldNotBeWildcard = components.subList(1, components.size());
		} else {
			matchesOnlyRoot = true;
		}
		if(componentsThatShouldNotBeWildcard.isEmpty()) {
			throw new SAXException(String.format("digester-rules.xml: A pattern that is only a wildcard is invalid. Encountered [%s]", originalPattern));
		}
		if(componentsThatShouldNotBeWildcard.stream().anyMatch(s -> s.equals("*"))) {
			throw new SAXException(String.format("digester-rules.xml: Only the first pattern component can be a wildcard. Encountered [%s]", originalPattern));
		}
		if(componentsThatShouldNotBeWildcard.size() >= 2) {
			List<String> violationCheckWords = new ArrayList<>(componentsThatShouldNotBeWildcard.subList(0, componentsThatShouldNotBeWildcard.size() - 1));
			Collections.reverse(violationCheckWords);
			matcher = new Matcher(violationCheckWords);
			matcher.setPatternOnlyMatchesRoot(matchesOnlyRoot);
		}
	}

	String getRoleName() {
		return components.getLast();
	}

	@Override
	public String toString() {
		return originalPattern;
	}

	static class Matcher {
		private @Getter @Setter boolean patternOnlyMatchesRoot = false;
		private final List<String> backtrackRoleNames;

		Matcher(List<String> backtrackRoleNames) {
			this.backtrackRoleNames = backtrackRoleNames;
		}

		boolean matches(FrankElement frankElement) {
			return checkOwners(Collections.singletonList(frankElement), backtrackRoleNames);
		}

		boolean checkChildren(List<ConfigChild> configChildren, List<String> remainingBacktrackRoleNames) {
			List<FrankElement> owners = configChildren.stream().map(ConfigChild::getOwningElement).toList();
			return checkOwners(owners, remainingBacktrackRoleNames);
		}

		boolean checkOwners(List<FrankElement> owners, List<String> remainingBacktrackRoleNames) {
			boolean haveMatchForRoot = owners.stream()
					.filter(RootFrankElement.class::isInstance)
					.map(f -> (RootFrankElement) f)
					.anyMatch(f -> f.getRoleName().equals(remainingBacktrackRoleNames.getFirst()));
			if(remainingBacktrackRoleNames.size() == 1) {
				if(haveMatchForRoot) {
					return true;
				} else if(patternOnlyMatchesRoot) {
					return false;
				}
			}
			List<ConfigChild> parents = owners.stream()
					.flatMap(f -> f.getConfigParents().stream())
					.filter(c -> c.getRoleName().equals(remainingBacktrackRoleNames.getFirst()))
					.toList();
			if(parents.isEmpty()) {
				return false;
			} else if(remainingBacktrackRoleNames.size() == 1) {
				return true;
			} else {
				return checkChildren(parents, remainingBacktrackRoleNames.subList(1, remainingBacktrackRoleNames.size()));
			}
		}

		@Override
		public String toString() {
			String result = "Matcher backtracking(" + String.join(", ", backtrackRoleNames) + ")";
			if(patternOnlyMatchesRoot) {
				result += " at root";
			}
			return result;
		}
	}
}
