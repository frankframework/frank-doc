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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FrankElementXsdElementNameTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.simple.";
	private static final String CONTAINER = PACKAGE + "Container";
	private static final String DIGESTER_RULES = "doc/xsd-element-name-digester-rules.xml";

	private FrankClassRepository classRepository;
	private FrankDocModel model;

	@BeforeEach
	public void setUp() throws IOException {
		classRepository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE, FRANK_DOC_GROUP_VALUES_PACKAGE);
		model = FrankDocModel.populate(TestUtil.resourceAsURL(DIGESTER_RULES), CONTAINER, classRepository);
	}

	@Test
	public void whenNameDoesNotEndWithInterfaceNameThenGroupRoleNameAppended() throws Exception {
		String className = PACKAGE + "ListenerParent";
		String typeName = PACKAGE + "IListener";
		FrankElement instance = model.findFrankElement(className);
		ElementType elementType = model.findOrCreateElementType(classRepository.findClass(typeName));
		String actual = instance.getXsdElementName(elementType, "testListener");
		assertEquals("ListenerParentTestListener", actual);
	}

	@Test
	public void whenNameEndsWithInterfaceNameThenRemovedAndGroupRoleNameAppended() throws Exception {
		String className = PACKAGE + "ParentListener";
		String typeName = PACKAGE + "IListener";
		FrankElement instance = model.findFrankElement(className);
		ElementType elementType = model.findOrCreateElementType(classRepository.findClass(typeName));
		String actual = instance.getXsdElementName(elementType, "testListener");
		assertEquals("ParentTestListener", actual);
	}

	@Test
	public void whenElementTypeIsNotInterfaceThenRoleNameBecomesElementName() throws Exception {
		String classAndTypeName = PACKAGE + "ListenerParent";
		FrankElement instance = model.findFrankElement(classAndTypeName);
		ElementType elementType = model.findOrCreateElementType(classRepository.findClass(classAndTypeName));
		String actual = instance.getXsdElementName(elementType, "someName");
		assertEquals("SomeName", actual);
	}

	@Test
	public void frankElementKnowsXmlElementNames() {
		FrankElement frankElement = model.findFrankElement(PACKAGE + "ListenerParent");
		assertArrayEquals(new String[] {"ListenerParentTestListener"}, frankElement.getXmlElementNames().toArray());
		frankElement = model.findFrankElement(PACKAGE + "ParentListener");
		assertArrayEquals(new String[] {"ParentTestListener"}, frankElement.getXmlElementNames().toArray());
		frankElement = model.findFrankElement(PACKAGE + "Container");
		assertArrayEquals(new String[] {"Container"}, frankElement.getXmlElementNames().toArray());
	}

	@Test
	public void whenFrankElementIsInterfaceBasedThenFlagSet() {
		FrankElement frankElement = model.findFrankElement(PACKAGE + "ListenerParent");
		assertTrue(frankElement.isInterfaceBased());
	}

	@Test
	public void whenFrankElementIsNotInterfaceBasedThenFlagNotSet() {
		FrankElement frankElement = model.findFrankElement(CONTAINER);
		assertFalse(frankElement.isInterfaceBased());
	}
}
