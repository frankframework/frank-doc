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

import lombok.EqualsAndHashCode;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;
import static org.frankframework.frankdoc.model.ElementChild.ALL_NOT_EXCLUDED;
import static org.frankframework.frankdoc.model.ElementChild.EXCLUDED;
import static org.frankframework.frankdoc.model.ElementChild.IN_XSD;
import static org.frankframework.frankdoc.model.ElementChild.REJECT_DEPRECATED;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NavigationTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.walking";

	public static Collection<Object[]> data() {
		return asList(new Object[][]{
			{"Parent", IN_XSD, EXCLUDED, List.of(ref(RefKind.DECLARED, "Parent"))},
			// Attribute childAttribute is not selected, so we do not have a real override.
			{"Child", IN_XSD, EXCLUDED, asList(ref(RefKind.DECLARED, "Child"), ref(RefKind.DECLARED, "Parent"))},
			// Attribute parentAttributeFirst is overridden. Keep with Child, omit with Parent
			{"Child", ALL_NOT_EXCLUDED, EXCLUDED, asList(ref(RefKind.DECLARED, "Child"), ref(RefKind.CHILD_TOP_LEVEL, "parentAttributeSecond"))},
			// All attributes of Parent were overridden. Nothing to reference for Parent.
			{"GrandChild", ALL_NOT_EXCLUDED, EXCLUDED, asList(ref(RefKind.DECLARED, "GrandChild"), ref(RefKind.DECLARED, "Child"))},
			// The override of parentAttributeSecond counts, in Child parentAttributeFirst is ignored as child
			{"GrandChild2", ALL_NOT_EXCLUDED, EXCLUDED, asList(ref(RefKind.DECLARED, "GrandChild2"), ref(RefKind.CUMULATIVE, "Child2"))},
			// All children of Child2 are deprecated, so Child2 is ignored in the ancestor hierarchy
			{"GrandChild2", IN_XSD, EXCLUDED, asList(ref(RefKind.DECLARED, "GrandChild2"), ref(RefKind.DECLARED, "Parent"))},
			// All attributes of Parent are overridden by deprecated methods and should be de-inherited
			{"GrandChild3", IN_XSD, REJECT_DEPRECATED, List.of()},
			// Same as above, but requires the algorithm to work around a technical override
			{"GrandChild5", IN_XSD, REJECT_DEPRECATED, List.of()},
			// Below Parent are technical overrides in GrandParent6. We test here that we
			// dont get Child6 which has no children, but Parent where the children are.
			{"GrandChild6", IN_XSD, REJECT_DEPRECATED, List.of(ref(RefKind.DECLARED, "Parent"))},
			// Test RefKind.CHILD
			{"GrandChild7", IN_XSD, EXCLUDED, asList(ref(RefKind.DECLARED, "GrandChild7"), ref(RefKind.CHILD, "childAttributeFirst"), ref(RefKind.DECLARED, "Parent"))},
			// Test fix of issue: Invalid XSDs generated #100.
			// When an attribute (or config child) is overridden by an attribute that is not selected, then
			// the cumulative group was erroneously taken. Instead, the algorithm should use the ancestor
			// of the overridden attribute that is selected.
			{"GrandChild8", IN_XSD, REJECT_DEPRECATED, asList(ref(RefKind.DECLARED, "GrandChild8"), ref(RefKind.CHILD_TOP_LEVEL, "parentAttributeSecond"))}
		});
	}

	private enum RefKind {
		CHILD,
		CHILD_TOP_LEVEL,
		DECLARED,
		CUMULATIVE;
	}

	@EqualsAndHashCode
	private static class Ref {
		private final RefKind kind;
		private final String name;

		Ref(RefKind kind, String name) {
			this.kind = kind;
			this.name = name;
		}

		@Override
		public String toString() {
			return "(" + kind.toString() + ", " + name + ")";
		}
	}

	private static Ref ref(RefKind kind, String name) {
		return new Ref(kind, name);
	}

	@MethodSource("data")
	@ParameterizedTest(name = "{0}")
	void test(String simpleClassName, Predicate<ElementChild> childSelector, Predicate<ElementChild> childRejector, List<Ref> expectedRefs) throws Exception {
		String rootClassName = PACKAGE + "." + simpleClassName;
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE, FRANK_DOC_GROUP_VALUES_PACKAGE);
		FrankDocModel model = FrankDocModel.populate(TestUtil.resourceAsURL("doc/empty-digester-rules.xml"), null, rootClassName, repository);
		FrankElement walkFrom = model.findFrankElement(rootClassName);
		List<Ref> actual = new ArrayList<>();
		walkFrom.walkCumulativeAttributes(new CumulativeChildHandler<>() {
			@Override
			public void handleSelectedChildren(List<FrankAttribute> children, FrankElement owner) {
				children.forEach(c -> actual.add(ref(RefKind.CHILD, c.getName())));
			}

			@Override
			public void handleSelectedChildrenOfTopLevel(List<FrankAttribute> children, FrankElement owner) {
				children.forEach(c -> actual.add(ref(RefKind.CHILD_TOP_LEVEL, c.getName())));
			}

			@Override
			public void handleChildrenOf(FrankElement frankElement) {
				actual.add(ref(RefKind.DECLARED, frankElement.getSimpleName()));
			}

			@Override
			public void handleCumulativeChildrenOf(FrankElement frankElement) {
				actual.add(ref(RefKind.CUMULATIVE, frankElement.getSimpleName()));
			}

		}, childSelector, childRejector);
		assertEquals(expectedRefs, actual);
	}

}
