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

import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigChildSetterDescriptorTest {
	private FrankDocModel instance;
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.technical.override."; // Doesn't matter which package we choose. This test doesn't depend on a package.

	@BeforeEach
	public void setUp() throws SAXException, IOException {
		// No need to set include and exclude filters of the FrankClassRepository, because
		// we are not asking for the implementations of an interface.
		instance = new FrankDocModel(TestUtil.getFrankClassRepositoryDoclet(PACKAGE, FRANK_DOC_GROUP_VALUES_PACKAGE), null);
		instance.createConfigChildDescriptorsFrom(TestUtil.resourceAsURL("doc/fake-digester-rules.xml"));
	}

	@Test
	public void whenSingularRuleThenSingularInDictionary() {
		ConfigChildSetterDescriptor configChildDescriptor = instance.getConfigChildDescriptors().get("setItemSingular").get(0);
		assertNotNull(configChildDescriptor);
		assertTrue(configChildDescriptor.isForObject());
		assertEquals("setItemSingular", configChildDescriptor.getMethodName());
		assertEquals("roleNameItemSingular", configChildDescriptor.getRoleName());
		assertFalse(configChildDescriptor.isAllowMultiple());
	}

	@Test
	public void whenPluralAddRuleThenPluralInDictionary() {
		ConfigChildSetterDescriptor configChildDescriptor = instance.getConfigChildDescriptors().get("addItemPlural").get(0);
		assertNotNull(configChildDescriptor);
		assertTrue(configChildDescriptor.isForObject());
		assertEquals("addItemPlural", configChildDescriptor.getMethodName());
		assertEquals("roleNameItemPluralAdd", configChildDescriptor.getRoleName());
		assertTrue(configChildDescriptor.isAllowMultiple());
	}

	@Test
	public void whenPluralRegisterThenPluralInDictionary() {
		ConfigChildSetterDescriptor configChildDescriptor = instance.getConfigChildDescriptors().get("registerItemPlural").get(0);
		assertNotNull(configChildDescriptor);
		assertTrue(configChildDescriptor.isForObject());
		assertEquals("registerItemPlural", configChildDescriptor.getMethodName());
		assertEquals("roleNameItemPluralRegister", configChildDescriptor.getRoleName());
		assertTrue(configChildDescriptor.isAllowMultiple());
	}

	@Test
	public void onlyRulesWithRegisterMethodsGoInDictionary() {
		assertEquals(4, instance.getConfigChildDescriptors().size());
	}

	@Test
	public void whenHasRegisterTextMethodThenTextConfigChild() {
		ConfigChildSetterDescriptor configChildDescriptor = instance.getConfigChildDescriptors().get("registerText").get(0);
		assertNotNull(configChildDescriptor);
		assertFalse(configChildDescriptor.isForObject());
		assertEquals("registerText", configChildDescriptor.getMethodName());
		assertEquals("roleNameText", configChildDescriptor.getRoleName());
		assertTrue(configChildDescriptor.isAllowMultiple());
	}

	@Test
	public void whenNoRuleThenNotInDictionary() {
		assertNull(instance.getConfigChildDescriptors().get("xyz"));
	}
}
