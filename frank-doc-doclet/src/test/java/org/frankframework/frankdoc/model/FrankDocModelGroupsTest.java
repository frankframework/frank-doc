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
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FrankDocModelGroupsTest {
	private FrankDocModel instance;

	@Test
	public void testGroups() throws IOException {
		String thePackage = "org.frankframework.frankdoc.testtarget.groups.";
		FrankClassRepository r = TestUtil.getFrankClassRepositoryDoclet(thePackage, FRANK_DOC_GROUP_VALUES_PACKAGE);
		instance = FrankDocModel.populate(TestUtil.resourceAsURL("doc/fake-group-digester-rules.xml"), null, thePackage + "Container", r);
		List<FrankDocGroup> groups = instance.getGroups();
		assertEquals(2, groups.size());

		FrankDocGroup current = groups.get(0);
		assertEquals("Listener", current.getName());
		List<ElementType> types = current.getElementTypes();
		assertEquals(1, types.size());
		assertEquals("IChild", types.get(0).getSimpleName());

		current = groups.get(1);
		assertEquals("Other", current.getName());
		types = current.getElementTypes();
		assertEquals(1, types.size());
		assertEquals("ISender", types.get(0).getSimpleName());

		// We test here that class DefaultSender is excluded from the type, because that would
		// produce a conflicting definition for XML tag "DefaultSender".
		List<FrankElement> frankElements = types.get(0).getSyntax2Members();
		assertEquals("Default", frankElements.get(0).getSimpleName());
		assertEquals(1, frankElements.size());
		assertTrue(frankElements.get(0).getXmlElementNames().contains("DefaultSender"));
		List<FrankElement> leftOvers = instance.getElementsOutsideConfigChildren();
		assertEquals(1, leftOvers.size());
		assertEquals("Container", leftOvers.get(0).getSimpleName());
	}
}
