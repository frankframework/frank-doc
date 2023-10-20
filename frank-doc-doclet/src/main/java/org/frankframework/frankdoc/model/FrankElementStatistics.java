/*
Copyright 2020 WeAreFrank!

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

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.frankframework.frankdoc.model.ElementChild.ALL_NOT_EXCLUDED;

public class FrankElementStatistics {
	private @Getter FrankElement subject;
	private @Getter int numAncestors;
	private @Getter int numChildren;
	private @Getter int numDescendants;
	private @Getter int numConfigChildren;
	private @Getter int numAttributes;
	private @Getter int numOverriddenConfigChildren;
	private @Getter int numOverriddenAttributes;
	private @Getter int numConfigChildOverriders;
	private @Getter int numAttributeOverriders;
	private @Getter int minConfigChildOverriderDepth;
	private @Getter int maxConfigChildOverriderDepth;
	private @Getter int minAttributeOverriderDepth;
	private @Getter int maxAttributeOverriderDepth;

	FrankElementStatistics(FrankElement subject) {
		this.subject = subject;
		if (subject.getParent() == null) {
			numAncestors = 0;
		} else {
			numAncestors = subject.getParent().getStatistics().getNumAncestors() + 1;
		}
	}

	public static String header() {
		return Arrays.asList(
				"FullName",
				"numAncestors",
				"numChildren",
				"numDescendants",
				"numConfigChildren",
				"numAttributes",
				"numOverriddenConfigChildren",
				"numOverriddenAttributes",
				"numConfigChildOverriders",
				"numAttributeOverriders",
				"minConfigChildOverriderDepth",
				"maxConfigChildOverriderDepth",
				"minAttributeOverriderDepth",
				"maxAttributeOverriderDepth")
			.stream().collect(Collectors.joining(", "));
	}

	@Override
	public String toString() {
		return String.join(", ", Arrays.asList(
			subject.getFullName(),
			Integer.toString(numAncestors),
			Integer.toString(numChildren),
			Integer.toString(numDescendants),
			Integer.toString(numConfigChildren),
			Integer.toString(numAttributes),
			Integer.toString(numOverriddenConfigChildren),
			Integer.toString(numOverriddenAttributes),
			Integer.toString(numConfigChildOverriders),
			Integer.toString(numAttributeOverriders),
			Integer.toString(minConfigChildOverriderDepth),
			Integer.toString(maxConfigChildOverriderDepth),
			Integer.toString(minAttributeOverriderDepth),
			Integer.toString(maxAttributeOverriderDepth)));
	}

	void finish() {
		finishDescendantStatistics();
		finishConfigChildOverrideStatistics();
		finishAttributeOverrideStatistics();
	}

	private void finishDescendantStatistics() {
		numConfigChildren = subject.getConfigChildren(ALL_NOT_EXCLUDED).size();
		numAttributes = subject.getAttributes(ALL_NOT_EXCLUDED).size();
		boolean isFirstAncestor = true;
		FrankElement ancestor = subject;
		while (ancestor.getParent() != null) {
			ancestor = ancestor.getParent();
			ancestor.getStatistics().numDescendants++;
			if (isFirstAncestor) {
				ancestor.getStatistics().numChildren++;
				isFirstAncestor = false;
			}
		}
	}

	private void finishConfigChildOverrideStatistics() {
		for (ConfigChild configChild : subject.getConfigChildren(ALL_NOT_EXCLUDED)) {
			if (configChild.getOverriddenFrom() != null) {
				numOverriddenConfigChildren++;
				addConfigChildOverriddenFrom(configChild.getOverriddenFrom());
			}
		}
	}

	private void addConfigChildOverriddenFrom(FrankElement overriddenFrom) {
		overriddenFrom.getStatistics().onConfigChildOverriderAtDepth(inheritanceDistance(overriddenFrom));
	}

	private int inheritanceDistance(FrankElement targetAncestor) {
		int depth = 0;
		FrankElement ancestor = subject;
		while (ancestor != targetAncestor) {
			ancestor = ancestor.getParent();
			depth++;
		}
		return depth;
	}

	private void onConfigChildOverriderAtDepth(int depth) {
		numConfigChildOverriders++;
		if (minConfigChildOverriderDepth == 0) {
			minConfigChildOverriderDepth = depth;
			maxConfigChildOverriderDepth = depth;
		} else {
			if (depth < minConfigChildOverriderDepth) {
				minConfigChildOverriderDepth = depth;
			}
			if (depth > maxConfigChildOverriderDepth) {
				maxConfigChildOverriderDepth = depth;
			}
		}
	}

	private void finishAttributeOverrideStatistics() {
		for (FrankAttribute attribute : subject.getAttributes(ALL_NOT_EXCLUDED)) {
			if (attribute.getOverriddenFrom() != null) {
				numOverriddenAttributes++;
				addAttributeOverriddenFrom(attribute.getOverriddenFrom());
			}
		}
	}

	private void addAttributeOverriddenFrom(FrankElement overriddenFrom) {
		overriddenFrom.getStatistics().onAttributeOverriderAtDepth(inheritanceDistance(overriddenFrom));
	}

	private void onAttributeOverriderAtDepth(int depth) {
		numAttributeOverriders++;
		if (minAttributeOverriderDepth == 0) {
			minAttributeOverriderDepth = depth;
			maxAttributeOverriderDepth = depth;
		} else {
			if (depth < minAttributeOverriderDepth) {
				minAttributeOverriderDepth = depth;
			}
			if (depth > maxAttributeOverriderDepth) {
				maxAttributeOverriderDepth = depth;
			}
		}
	}
}
