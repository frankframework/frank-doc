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

import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;
import static org.frankframework.frankdoc.model.ElementChild.ALL_NOT_EXCLUDED;
import static org.frankframework.frankdoc.model.ElementChild.EXCLUDED;
import static org.frankframework.frankdoc.model.ElementChild.IN_XSD;
import static org.frankframework.frankdoc.model.ElementChild.REJECT_DEPRECATED;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NavigationCumulativeTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.walking";

	public static Collection<Object[]> data() {
		return asList(new Object[][] {
			{"Parent", "Parent", IN_XSD, EXCLUDED, asList("parentAttributeFirst", "parentAttributeSecond")},
			{"Child inXsd", "Child", IN_XSD, EXCLUDED, asList("childAttribute", "parentAttributeFirst", "parentAttributeSecond")},
			{"Child all", "Child", ALL_NOT_EXCLUDED, EXCLUDED, asList("parentAttributeFirst", "childAttribute", "parentAttributeSecond")},
			{"GrandChild", "GrandChild", ALL_NOT_EXCLUDED, EXCLUDED, asList("parentAttributeSecond", "grandChildAttribute", "parentAttributeFirst", "childAttribute")},
			{"GrandChild2 no reject", "GrandChild2", ALL_NOT_EXCLUDED, EXCLUDED, asList("grandChild2Attribute", "child2Attribute", "parentAttributeFirst", "parentAttributeSecond")},
			{"GrandChild2 reject deprecated", "GrandChild2", IN_XSD, REJECT_DEPRECATED, asList("grandChild2Attribute", "parentAttributeFirst", "parentAttributeSecond")}
		});
	}

	@MethodSource("data")
	@ParameterizedTest(name = "{0} with {1} and {2}")
	void test(String title, String simpleClassName, Predicate<ElementChild> childSelector, Predicate<ElementChild> childRejector, List<String> childNames) throws Exception {
		String rootClassName = PACKAGE + "." + simpleClassName;
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE, FRANK_DOC_GROUP_VALUES_PACKAGE);
		FrankDocModel model = FrankDocModel.populate(TestUtil.resourceAsURL("doc/empty-digester-rules.xml"), rootClassName, repository);
		FrankElement subject = model.findFrankElement(rootClassName);
		List<String> actual = subject.getCumulativeAttributes(childSelector, childRejector).stream()
				.map(FrankAttribute::getName).collect(Collectors.toList());
		assertEquals(childNames, actual);
	}

}
