package org.frankframework.frankdoc.model;

import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FrankDocModelAttributeTypeTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.enumattr.";
	private static final String CHILD = PACKAGE + "Child";
	private static final String MY_ENUM = PACKAGE + "MyEnum";

	private FrankClassRepository classRepository;

	@BeforeEach
	public void setUp() {
		classRepository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE, FRANK_DOC_GROUP_VALUES_PACKAGE);
	}

	@Test
	public void testGetEnumGettersByAttributeName() throws Exception {
		Map<String, FrankMethod> actual = FrankDocModel.getEnumGettersByAttributeName(classRepository.findClass(CHILD));
		assertTrue(actual.containsKey("parentAttribute"));
		assertTrue(actual.containsKey("childStringAttribute"));
		assertEquals(2, actual.size());
	}

	@Test
	public void testPopulate() throws IOException {
		FrankDocModel model = FrankDocModel.populate(TestUtil.resourceAsURL("doc/empty-digester-rules.xml"), null, CHILD, classRepository);
		FrankElement child = model.findFrankElement(CHILD);
		assertNotNull(child);
		// Test the attribute with a value list, which is of type STRING.
		AttributeEnum myEnum = model.findAttributeEnum(MY_ENUM);
		assertEquals(MY_ENUM, myEnum.getFullName());
		String[] actualLabels = myEnum.getValues().stream().map(EnumValue::getLabel).collect(Collectors.toList()).toArray(new String[] {});
		assertArrayEquals(new String[] {"TWO", "customLabelOne", "THREE"}, actualLabels);
		EnumValue v = myEnum.getValues().get(0);
		// This one has no annotation and no description.
		assertEquals("TWO", v.getLabel());
		assertNull(v.getDescription());
		// This one has a custom label and a description
		v = myEnum.getValues().get(1);
		assertEquals("customLabelOne", v.getLabel());
		assertEquals("Description of customLabelOne <code>MrBean</code>.", v.getDescription());

		// By fixing the list index like this, we test that the attributes are sorted correctly.
		FrankAttribute childAttribute = child.getAttributes(ElementChild.ALL_NOT_EXCLUDED).get(0);
		assertEquals("childStringAttribute", childAttribute.getName());
		assertEquals(myEnum, childAttribute.getAttributeEnum());
		assertEquals(AttributeType.STRING, childAttribute.getAttributeType());
		// Test the int attribute
		childAttribute = child.getAttributes(ElementChild.ALL_NOT_EXCLUDED).get(1);
		assertEquals(AttributeType.INT, childAttribute.getAttributeType());
	}

	@Test
	public void whenAttributeSetterTakesEnumThenEnumTypedAttribute() throws IOException {
		FrankDocModel model = FrankDocModel.populate(TestUtil.resourceAsURL("doc/empty-digester-rules.xml"), null, CHILD, classRepository);
		FrankElement child = model.findFrankElement(CHILD);
		assertNotNull(child);
		// By taking a fixed element index we test that the attributes appear in the right order.
		FrankAttribute childAttribute = child.getAttributes(ElementChild.ALL_NOT_EXCLUDED).get(2);
		assertEquals("enumSetterAttribute", childAttribute.getName());
		AttributeType attributeType = childAttribute.getAttributeType();
		assertEquals(AttributeType.STRING, attributeType);
		AttributeEnum attributeEnum = childAttribute.getAttributeEnum();
		assertEquals("MyOtherEnum", attributeEnum.getUniqueName(""));
		assertEquals(PACKAGE + "MyOtherEnum", attributeEnum.getFullName());
		List<EnumValue> values = attributeEnum.getValues();
		assertEquals("OTHER_ENUM_FIRST", values.get(0).getLabel());
		assertEquals("OTHER_ENUM_SECOND", values.get(1).getLabel());
		assertSame(attributeEnum, model.findAttributeEnum(PACKAGE + "MyOtherEnum"));
	}
}
