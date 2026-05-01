/*
Copyright 2021-2024 WeAreFrank!

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

import org.frankframework.frankdoc.TestAppender;
import org.frankframework.frankdoc.feature.Reference;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;
import static org.frankframework.frankdoc.model.ElementChild.ALL_NOT_EXCLUDED;
import static org.frankframework.frankdoc.model.ElementChild.IN_XSD;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FrankDocModelTest {
	private static final String SIMPLE = "org.frankframework.frankdoc.testtarget.simple";
	private static final String LISTENER = SIMPLE + ".IListener";
	private static final String SIMPLE_PARENT = SIMPLE + ".ListenerParent";
	private static final String SIMPLE_CHILD = SIMPLE + ".ListenerChild";
	private static final String SIMPLE_GRAND_CHILD = SIMPLE + ".ListenerGrandChild";
	private static final String SIMPLE_GRAND_PARENT = SIMPLE + ".AbstractGrandParent";
	private static final String FOR_XSD_ELEMENT_NAME_TEST = SIMPLE + ".ParentListener";

	private static final String IBISDOCREF = "org.frankframework.frankdoc.testtarget.ibisdocref";
	private static final String REFERRER = "org.frankframework.frankdoc.testtarget.ibisdocref.Referrer";

	FrankClassRepository classRepository;

	private FrankDocModel instance;
	private FrankElement attributeOwner;

	@BeforeEach
	public void setUp() {
		String[] allPackages = new String[] {SIMPLE, IBISDOCREF, "org.frankframework.frankdoc.testtarget.reflect", FRANK_DOC_GROUP_VALUES_PACKAGE};
		classRepository = TestUtil.getFrankClassRepositoryDoclet(allPackages);
		instance = new FrankDocModel(classRepository, null);
		attributeOwner = null;
	}

	@Test
	public void whenInterfaceTypeAndSingletonTypeThenCorrectElements() throws FrankDocException {
		ElementType listenerType = instance.findOrCreateElementType(classRepository.findClass(LISTENER));
		ElementType childType = instance.findOrCreateElementType(classRepository.findClass(SIMPLE_CHILD));
		checkModelTypes(listenerType, childType);
	}

	@Test
	public void whenSingletonTypeAndInterfaceTypeThenCorrectElements() throws FrankDocException {
		ElementType childType = instance.findOrCreateElementType(classRepository.findClass(SIMPLE_CHILD));
		ElementType listenerType = instance.findOrCreateElementType(classRepository.findClass(LISTENER));
		checkModelTypes(listenerType, childType);
	}

	private void checkModelTypes(ElementType actualListener, ElementType actualChild) {
		assertTrue(instance.hasType(actualListener.getFullName()));
		assertTrue(instance.hasType(actualChild.getFullName()));
		assertSame(instance.getAllTypes().get(LISTENER), actualListener);
		assertSame(instance.getAllTypes().get(SIMPLE_CHILD), actualChild);
		assertSame(instance.getAllElements().get(SIMPLE_CHILD), getMember(actualChild.getMembers(), SIMPLE_CHILD));
		assertTrue(instance.getAllElements().containsKey(SIMPLE_PARENT));
		assertTrue(instance.getAllElements().containsKey(FOR_XSD_ELEMENT_NAME_TEST));
		List<FrankElement> listenerMembers = actualListener.getMembers();
		// Tests that AbstractGrandParent is omitted.
		assertEquals(4, listenerMembers.size());
		assertTrue(membersContain(listenerMembers, SIMPLE_PARENT));
		assertTrue(membersContain(listenerMembers, SIMPLE_CHILD));
		assertTrue(membersContain(listenerMembers, SIMPLE_GRAND_CHILD));
		List<FrankElement> childMembers = actualChild.getMembers();
		assertEquals(1, childMembers.size());
		assertTrue(membersContain(childMembers, SIMPLE_CHILD));
		assertEquals(LISTENER, actualListener.getFullName());
		assertEquals("IListener", actualListener.getSimpleName());
		assertEquals(SIMPLE_CHILD, actualChild.getFullName());
		assertEquals("ListenerChild", actualChild.getSimpleName());
	}

	private boolean membersContain(List<FrankElement> elements, String fullName) {
		return elements.stream().anyMatch(elem -> elem.getFullName().equals(fullName));
	}

	private FrankElement getMember(List<FrankElement> elements, String fullName) {
		for(FrankElement element: elements) {
			if(element.getFullName().equals(fullName)) {
				return element;
			}
		}
		return null;
	}

	@Test
	public void whenTypeRequestedTwiceThenSameInstanceReturned() throws FrankDocException {
		ElementType first = instance.findOrCreateElementType(classRepository.findClass(SIMPLE_CHILD));
		ElementType second = instance.findOrCreateElementType(classRepository.findClass(SIMPLE_CHILD));
		assertSame(first, second);
	}

	@Test
	public void whenChildElementAddedBeforeParentThenCorrectModel() throws FrankDocException {
		FrankElement child = instance.findOrCreateFrankElement(SIMPLE_CHILD);
		FrankElement parent = instance.findOrCreateFrankElement(SIMPLE_PARENT);
		instance.findOrCreateFrankElement(SIMPLE_GRAND_CHILD);
		instance.setOverriddenFrom();
		checkModelAfterChildAndParentAdded(parent, child);
	}

	@Test
	public void whenParentElementAddedBeforeChildThenCorrectModel() throws FrankDocException {
		FrankElement parent = instance.findOrCreateFrankElement(SIMPLE_PARENT);
		FrankElement child = instance.findOrCreateFrankElement(SIMPLE_CHILD);
		instance.findOrCreateFrankElement(SIMPLE_GRAND_CHILD);
		instance.setOverriddenFrom();
		checkModelAfterChildAndParentAdded(parent, child);
	}

	private void checkModelAfterChildAndParentAdded(FrankElement actualParent, FrankElement actualChild) {
		Map<String, FrankElement> actualAllElements = instance.getAllElements();
		assertTrue(actualAllElements.containsKey(actualParent.getFullName()));
		assertTrue(actualAllElements.containsKey(actualChild.getFullName()));
		assertSame(actualAllElements.get(actualParent.getFullName()), actualParent);
		assertSame(actualAllElements.get(actualChild.getFullName()), actualChild);
		FrankElement actualGrandParent = actualAllElements.get(SIMPLE_GRAND_PARENT);
		assertTrue(actualGrandParent.isAbstract());
		FrankElement actualObject = actualAllElements.get("java.lang.Object");
		assertNull(actualObject.getParent());
		assertSame(actualObject, actualGrandParent.getParent());
		assertSame(actualGrandParent, actualParent.getParent());
		assertEquals(SIMPLE_PARENT, actualParent.getFullName());
		assertEquals("ListenerParent", actualParent.getSimpleName());
		assertFalse(actualParent.isAbstract());
		// We check here that protected method getChildAttribute does not produce
		// an attribute
		assertEquals(3, actualParent.getAttributes(ALL_NOT_EXCLUDED).size());
		FrankAttribute actualParentAttribute = findAttribute(actualParent, "parentAttribute");
		assertEquals("parentAttribute", actualParentAttribute.getName());
		assertSame(actualParent, actualParentAttribute.getOwningElement());
		assertNull(actualParentAttribute.getOverriddenFrom());
		assertFalse(actualParentAttribute.isTechnicalOverride());
		FrankAttribute actualInheritedAttribute = findAttribute(actualParent, "inheritedAttribute");
		assertEquals("inheritedAttribute", actualInheritedAttribute.getName());
		assertNull(actualInheritedAttribute.getOverriddenFrom());
		assertSame(actualParent, actualChild.getParent());
		assertEquals(SIMPLE_CHILD, actualChild.getFullName());
		assertEquals("ListenerChild", actualChild.getSimpleName());
		assertEquals(4, actualChild.getAttributes(ALL_NOT_EXCLUDED).size());
		FrankAttribute actualChildAttribute = findAttribute(actualChild, "notTextConfigChildButAttribute");
		assertEquals("notTextConfigChildButAttribute", actualChildAttribute.getName());
		actualChildAttribute = findAttribute(actualChild, "childAttribute");
		assertEquals("childAttribute", actualChildAttribute.getName());
		assertSame(actualChild, actualChildAttribute.getOwningElement());
		assertNull(actualChildAttribute.getOverriddenFrom());
		actualInheritedAttribute = findAttribute(actualChild, "inheritedAttribute");
		assertEquals("inheritedAttribute", actualInheritedAttribute.getName());
		assertSame(actualParent, actualInheritedAttribute.getOverriddenFrom());
		FrankElement actualGrandChild = actualAllElements.get(SIMPLE_GRAND_CHILD);
		assertEquals(SIMPLE_GRAND_CHILD, actualGrandChild.getFullName());
		assertEquals(1, actualGrandChild.getAttributes(ALL_NOT_EXCLUDED).size());
		actualInheritedAttribute = actualGrandChild.getAttributes(ALL_NOT_EXCLUDED).get(0);
		assertEquals("inheritedAttribute", actualInheritedAttribute.getName());
		assertSame(actualChild, actualInheritedAttribute.getOverriddenFrom());
		// Deprecated
		FrankAttribute attribute = actualParent.getAttributes(a -> ((FrankAttribute) a).getName().equals(
				"deprecatedInParentAttribute")).get(0);
		assertTrue(attribute.isDeprecated());
		attribute = actualChild.getAttributes(a -> ((FrankAttribute) a).getName().equals(
				"deprecatedInParentAttribute")).get(0);
		assertFalse(attribute.isDeprecated());
	}

	private FrankAttribute findAttribute(final FrankElement elem, String name) {
		for(FrankAttribute attribute: elem.getAttributes(ALL_NOT_EXCLUDED)) {
			if(attribute.getName().contentEquals(name)) {
				return attribute;
			}
		}
		return null;
	}

	@Test
	public void whenSetterAndGetterThenAttribute() throws FrankDocException {
		checkReflectAttributeCreated("attributeSetterGetter");
	}

	private FrankAttribute checkReflectAttributeCreated(String attributeName) throws FrankDocException {
		Map<String, FrankAttribute> actual = getReflectInvestigatedFrankAttributes();
		assertTrue(actual.containsKey(attributeName));
		assertEquals(attributeName, actual.get(attributeName).getName());
		return actual.get(attributeName);
	}

	private Map<String, FrankAttribute> getReflectInvestigatedFrankAttributes() throws FrankDocException {
		return getAttributesOfClass("org.frankframework.frankdoc.testtarget.reflect.FrankAttributeTarget");
	}

	/**
	 * Asks the system-under-test class {@link FrankDocModel} for the FrankAttribute objects
	 * of a class. A dummy FrankElement is supplied as attribute owner, so the
	 * describingElement is only correct if it is parsed from an @IbisDocRef annotation.
	 */
	private Map<String, FrankAttribute> getAttributesOfClass(final String className) throws FrankDocException {
		attributeOwner = instance.findOrCreateFrankElement(className);
		final List<FrankAttribute> attributes = instance.createAttributes(classRepository.findClass(className), attributeOwner, classRepository);
		return attributes.stream().collect(Collectors.toMap(FrankAttribute::getName, att -> att));
	}

	@Test
	public void whenSetterAndIsThenAttribute() throws FrankDocException {
		checkReflectAttributeCreated("attributeSetterIs");
	}

	@Test
	public void whenOnlySetterThenAttribute() throws FrankDocException {
		FrankAttribute attribute = checkReflectAttributeCreated("attributeOnlySetter");
		assertFalse(attribute.isDocumented());
	}

	/**
	 * This test only has added value when testing with ClassDoc-s. This has
	 * to do with method {@link org.frankframework.frankdoc.Utils#isAttributeGetterOrSetter(FrankMethod)}.
	 * That method filters method using {@link org.frankframework.frankdoc.wrapper.FrankMethod#isVarargs()}.
	 * That filter is only needed when a varargs String argument appears as a String argument type.
	 * This might be the case for TypeElement but not for Java reflection. With reflection, a
	 * varargs String appears as type String[].
	 * <p>
	 * Probably, vararg strings arguments only appear as simple String arguments for ClassDoc-s
	 * if a doclet does not set the SourceVersion to JAVA_1_5. There was no need to investigate
	 * further, because the JAVA_1_5 language version is now set, see {@link org.frankframework.frankdoc.doclet.DocletBuilder#SourceVersion()}.
	 * Furthermore, filtering with isVarargs() certainly does no harm.
	 * @throws FrankDocException
	 */
	@Test
	public void whenArgIsVarargThenNotAttribute() throws FrankDocException {
		checkReflectAttributeOmitted("setNonAttributeVararg");
	}

	@Test
	public void whenSetterHasPrimitiveTypeThenAttribute() throws FrankDocException {
		FrankAttribute attribute = checkReflectAttributeCreated("attributeOnlySetterInt");
		assertFalse(attribute.isDocumented());
	}

	@Test
	public void whenSetterHasBoxedIntTypeThenAttribute() throws FrankDocException {
		checkReflectAttributeCreated("attributeOnlySetterIntBoxed");
	}

	@Test
	public void whenSetterHasBoxedBoolTypeThenAttribute() throws FrankDocException {
		checkReflectAttributeCreated("attributeOnlySetterBoolBoxed");
	}

	@Test
	public void whenSetterHasBoxedLongTypeThenAttribute() throws FrankDocException {
		checkReflectAttributeCreated("attributeOnlySetterLongBoxed");
	}

	@Test
	public void whenSetterHasBoxedByteTypeThenAttribute() throws FrankDocException {
		checkReflectAttributeCreated("attributeOnlySetterByteBoxed");
	}

	@Test
	public void whenSetterHasBoxedShortTypeThenAttribute() throws FrankDocException {
		checkReflectAttributeCreated("attributeOnlySetterShortBoxed");
	}

	@Test
	public void whenSetterHasEnumTypeThenAttribute() throws FrankDocException {
		checkReflectAttributeCreated("attributeSetterTakingEnum");
	}

	private void checkReflectAttributeOmitted(String attributeName) throws FrankDocException {
		Map<String, FrankAttribute> actual = getReflectInvestigatedFrankAttributes();
		assertFalse(actual.containsKey(attributeName));
	}

	@Test
	public void whenMethodsHaveWrongTypeThenNoAttribute() throws FrankDocException {
		checkReflectAttributeOmitted("noAttributeComplexType");
	}

	@Test
	public void whenAttributeNameMissesPrefixThenFilteredOutOfAttributes() throws FrankDocException {
		assertFalse(getAttributeNameMap("get").containsKey("Prefix"));
	}

	Map<String, String> getAttributeNameMap(String prefix) throws FrankDocException {
		Map<String, FrankMethod> attributeToMethodMap = FrankDocModel.getAttributeToMethodMap(
				classRepository.findClass("org.frankframework.frankdoc.testtarget.reflect.FrankAttributeTarget").getDeclaredMethods(), prefix);
		Map<String, String> result = new HashMap<>();
		for(String attributeName: attributeToMethodMap.keySet()) {
			result.put(attributeName, attributeToMethodMap.get(attributeName).getName());
		}
		return result;
	}

	@Test
	public void whenAttributeNameEqualsPrefixThenFilteredOutOfAttributes() throws FrankDocException {
		assertFalse(getAttributeNameMap("get").containsKey(""));
	}

	@Test
	public void whenSetterTakesTwoValuesThenNotSetter() throws FrankDocException {
		assertFalse(getAttributeNameMap("set").containsKey("invalidSetter"));
	}

	@Test
	public void whenSetterTakesNoValuesThenNoSetter() throws FrankDocException {
		assertFalse(getAttributeNameMap("set").containsKey("invalidSetterNoParams"));
	}

	@Test
	public void testSequenceOfAttributesMatchesSequenceOfSetterMethods() throws Exception {
		String className = "org.frankframework.frankdoc.testtarget.reflect.FrankAttributeTarget";
		attributeOwner = instance.findOrCreateFrankElement(className);
		List<String> actualAttributeNames = instance.createAttributes(classRepository.findClass(className), attributeOwner, classRepository).stream()
				.map(FrankAttribute::getName)
				.toList();
		String[] expectedAttributeNames = new String[] {"attributeSetterGetter", "attributeSetterIs", "attributeOnlySetter", "attributeVararg", "attributeOnlySetterInt",
				"attributeOnlySetterIntBoxed", "attributeOnlySetterBoolBoxed", "attributeOnlySetterLongBoxed", "attributeOnlySetterByteBoxed",
				"attributeOnlySetterShortBoxed", "ibisDockedOnlyDescription",
				"ibisDockedDeprecated", "attributeWithJavaDoc", "attributeWithInheritedJavaDoc",
				"attributeWithJavaDocDefault",
				"attributeWithInheritedJavaDocDefault", "attributeWithIbisDocThatOverrulesJavadocDefault",
				"attributeSetterTakingEnum"};
		assertArrayEquals(expectedAttributeNames, actualAttributeNames.toArray(new String[] {}));
	}

	@Test
	public void testIbisDockedOnlyDescription() throws FrankDocException {
		FrankAttribute actual = checkReflectAttributeCreated("ibisDockedOnlyDescription");
		assertFalse(actual.isDocumented());
		assertNull(actual.getDefaultValue());
		assertFalse(actual.isDeprecated());
	}

	@Test
	public void testIbisDockedDeprecated() throws FrankDocException {
		FrankAttribute actual = checkReflectAttributeCreated("ibisDockedDeprecated");
		assertTrue(actual.isDeprecated());
		assertFalse(IN_XSD.test(actual));
	}

	@Test
	public void whenAttributeHasJavaDocThenDocumentedAndDescription() throws Exception {
		FrankAttribute actual = checkReflectAttributeCreated("attributeWithJavaDoc");
		assertTrue(actual.isDocumented());
		assertEquals("Attribute with JavaDoc", actual.getDescription());
	}

	@Test
	public void whenAttributeHasJavaDocDefaultThenDocumentedAndDefault() throws Exception {
		FrankAttribute actual = checkReflectAttributeCreated("attributeWithJavaDocDefault");
		assertTrue(actual.isDocumented());
		assertEquals("My default value", actual.getDefaultValue());
	}

	@Test
	public void whenAttributeHasInheritedJavaDocThenNotDocumentedButDescription() throws Exception {
		FrankAttribute actual = checkReflectAttributeCreated("attributeWithInheritedJavaDoc");
		assertFalse(actual.isDocumented());
		assertEquals("JavaDoc of FrankAttributeTargetParent.setAttributeWithInheritedJavaDoc()", actual.getDescription());
	}

	@Test
	public void whenAttributeHasInheritedJavaDocDefaultThenNotDocumentedButDefault() throws Exception {
		FrankAttribute actual = checkReflectAttributeCreated("attributeWithInheritedJavaDocDefault");
		assertFalse(actual.isDocumented());
		assertEquals("My inherited default value", actual.getDefaultValue());
	}

	private FrankAttribute checkIbisdocrefInvestigatedFrankAttribute(String attributeName) throws FrankDocException {
		return checkIbisdocrefInvestigatedFrankAttribute(attributeName, REFERRER);
	}

	private FrankAttribute checkIbisdocrefInvestigatedFrankAttribute(String attributeName, String targetClassName) throws FrankDocException {
		Map<String, FrankAttribute> attributeMap = getAttributesOfClass(targetClassName);
		assertTrue(attributeMap.containsKey(attributeName));
		return attributeMap.get(attributeName);
	}

	@Test
	public void testFfReferWithInheritedDescriptionAndDefault() throws Exception {
		FrankAttribute actual = checkIbisdocrefInvestigatedFrankAttribute("ffReferInheritedDescription");
		assertTrue(actual.isDocumented());
		assertEquals(MandatoryStatus.OPTIONAL, actual.getMandatoryStatus());
		assertSame(attributeOwner, actual.getOwningElement());
		assertEquals("Description of setFfReferInheritedDescription", actual.getDescription());
		assertEquals("Value of setFfReferInheritedDescription", actual.getDefaultValue());
	}

	@Test
	public void testReferTo() throws Exception {
		FrankAttribute actual = checkIbisdocrefInvestigatedFrankAttribute("referToInheritedDescription");
		assertTrue(actual.isDocumented());
		assertEquals(MandatoryStatus.OPTIONAL, actual.getMandatoryStatus());
		assertSame(attributeOwner, actual.getOwningElement());
		assertEquals("setReferToInheritedDescription description", actual.getDescription());
	}

	@Test
	public void testReferToMissingMethod() throws Exception {
		try (TestAppender appender = TestAppender.newBuilder().build()) {
			Reference reference = new Reference(classRepository);
			FrankMethod targetMethod = Arrays.stream(classRepository.findClass(REFERRER).getDeclaredMethods()).filter(frankMethod -> frankMethod.getName().equals("doesNotExistsMethod")).findFirst().get();
			reference.valueOf(targetMethod);
			appender.assertLogged("Referred method [org.frankframework.frankdoc.testtarget.ibisdocref.ChildTargetParameterized] does not exist, as specified at location: [Referrer.doesNotExistsMethod]");
		}
	}

	@Test
	public void testReferToWithParameterizedTargetClass() throws Exception {
		FrankAttribute actual = checkIbisdocrefInvestigatedFrankAttribute("referToParameterizedType");
		assertTrue(actual.isDocumented());
		assertEquals(MandatoryStatus.OPTIONAL, actual.getMandatoryStatus());
		assertSame(attributeOwner, actual.getOwningElement());
		assertEquals("Description testing reference to parameterized class.", actual.getDescription());
	}

	@Test
	public void testFrankElementDeprecatedAttribute() throws Exception {
		FrankElement element = instance.findOrCreateFrankElement(SIMPLE + ".NonDeprecatedDescendant");
		assertNotNull(element);
		assertFalse(element.isDeprecated());
		assertTrue(element.getParent().isDeprecated());
	}
}
