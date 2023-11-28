package org.frankframework.frankdoc;

import org.frankframework.frankdoc.model.AttributeEnum;
import org.frankframework.frankdoc.model.AttributeType;
import org.frankframework.frankdoc.model.ElementChild;
import org.frankframework.frankdoc.model.FrankAttribute;
import org.frankframework.frankdoc.model.FrankDocModel;
import org.frankframework.frankdoc.util.XmlBuilder;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import static org.frankframework.frankdoc.DocWriterNewXmlUtils.addComplexType;
import static org.frankframework.frankdoc.DocWriterNewXmlUtils.addElementWithType;
import static org.frankframework.frankdoc.DocWriterNewXmlUtils.getXmlSchema;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class AttributeTypeStrategyTest {
	private String schemaStringAllowAttributeRef;
	private String schemaStringAllowAttributeRefEnumValuesIgnoreCase;

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{"NormalInteger", getIntTestXml("1"), true, true},
			{"IntegerWithPlus", getIntTestXml("+1"), true, true},
			{"NegativeInteger", getIntTestXml("-12"), true, true},
			{"RefAsInteger", getIntTestXml("${myVariable}"), true, true},
			{"NonIntegerForInteger", getIntTestXml("aString"), false, false},
			{"InvalidRefForInteger", getIntTestXml("${myVariable"), false, false},
			{"True", getBoolTestXml("true"), true, true},
			{"False", getBoolTestXml("false"), true, true},
			{"RefAsBoolean", getBoolTestXml("${myVariable}"), true, true},
			{"StringForBoolean", getBoolTestXml("aString"), false, false},
			{"InvalidRefForBoolean", getBoolTestXml("${myVariable"), false, false},
			{"AttributeActiveTrueLowerCase", getTestXmlActive("true"), true, true},
			{"AttributeActiveFalseLowerCase", getTestXmlActive("false"), true, true},
			{"AttributeActiveMixedCaseTrue", getTestXmlActive("True"), true, true},
			{"AttributeActiveMixedCaseFalse", getTestXmlActive("False"), true, true},
			{"RefAsAttributeActive", getTestXmlActive("${myVar}"), true, true},
			{"AttributeActiveNotTrue", getTestXmlActive("!true"), true, true},
			{"NegatedRefAsAttributeActive", getTestXmlActive("!${myVar}"), true, true},
			{"StringForAttributeActive", getTestXmlActive("xxx"), false, false},
			{"AttributeActiveConcatsMultipleValidValues", getTestXmlActive("${myVar}true"), false, false},
			{"RestrictedAttribute", getEnumTestXml("TWO"), true, true},
			{"RestrictedAttributeMixedCase1", getEnumTestXml("Two"), false, true},
			{"RestrictedAttributeMixedCase2", getEnumTestXml("twO"), false, true},
			{"InvalidValueRestrictedAttribute", getEnumTestXml("xxx"), false, false},
			{"ArbitraryString", getStringTestXml("xxx"), true, true},
			{"StringContainingVariableRef", getStringTestXml("${myVar}true"), true, true},
			{"VariableRefAsString", getStringTestXml("${myVar}"), true, true},
		});
	}

	private static String getBoolTestXml(String value) {
		return String.format("<myElement boolAttr=\"%s\"/>", value);
	}

	private static String getIntTestXml(String value) {
		return String.format("<myElement intAttr=\"%s\"/>", value);
	}

	private static String getStringTestXml(String value) {
		return String.format("<myElement stringAttr=\"%s\"/>", value);
	}

	private static String getEnumTestXml(String value) {
		return String.format("<myElement restrictedAttribute=\"%s\"/>", value);
	}

	private static String getTestXmlActive(String value) {
		return String.format("<myElement active=\"%s\"/>", value);
	}

	@Parameter(0)
	public String title;

	@Parameter(1)
	public String testXml;

	@Parameter(2)
	public boolean allowPropertyRefShouldAccept;

	@Parameter(3)
	public boolean allowPropertyRefEnumValuesIgnoreCaseShouldAccept;

	@Before
	public void setUp() throws IOException {
		String packageOfEnum = "org.frankframework.frankdoc.testtarget.attribute.type.strategy.";
		FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(packageOfEnum);
		String digesterRulesFileName = "doc/empty-digester-rules.xml";
		FrankDocModel model = FrankDocModel.populate(TestUtil.resourceAsURL(digesterRulesFileName), packageOfEnum + "Container", classRepository);
		FrankAttribute attribute = model.findFrankElement(packageOfEnum + "Container").getAttributes(ElementChild.ALL_NOT_EXCLUDED).get(0);
		AttributeEnum attributeEnum = model.findAttributeEnum(packageOfEnum + "Container.TestType");
		schemaStringAllowAttributeRef = getXsd(AttributeTypeStrategy.ALLOW_PROPERTY_REF, attributeEnum, attribute);
		schemaStringAllowAttributeRefEnumValuesIgnoreCase = getXsd(AttributeTypeStrategy.ALLOW_PROPERTY_REF_ENUM_VALUES_IGNORE_CASE, attributeEnum, attribute);
	}

	private static String getXsd(AttributeTypeStrategy attributeTypeStrategy, AttributeEnum attributeEnum, FrankAttribute enumTypedAttribute) {
		XmlBuilder schema = getXmlSchema("1.2.3-SNAPSHOT");
		XmlBuilder element = addElementWithType(schema, "myElement");
		XmlBuilder complexType = addComplexType(element);
		// We do not test mandatory attributes here. Therefore the fourth argument is "false".
		complexType.addSubElement(attributeTypeStrategy.createAttribute("boolAttr", AttributeType.BOOL));
		complexType.addSubElement(attributeTypeStrategy.createAttribute("intAttr", AttributeType.INT));
		complexType.addSubElement(attributeTypeStrategy.createAttribute("stringAttr", AttributeType.STRING));
		complexType.addSubElement(AttributeTypeStrategy.createAttributeActive());
		// This test does not test whether use="required" is included. It is about
		// attribute types. Therefore we take the attribute not to be mandatory.
		complexType.addSubElement(attributeTypeStrategy.createRestrictedAttribute(enumTypedAttribute, a -> {}));
		attributeTypeStrategy.createHelperTypes().forEach(h -> schema.addSubElement(h));
		schema.addSubElement(attributeTypeStrategy.createAttributeEnumType(attributeEnum));
		return schema.toXML(true);
	}

	@Test
	public void testAllowPropertyRef() {
		doTest(allowPropertyRefShouldAccept, schemaStringAllowAttributeRef);
	}

	@Test
	public void testAllowPropertyRefEnumValuesIgnoreCase() {
		doTest(allowPropertyRefEnumValuesIgnoreCaseShouldAccept, schemaStringAllowAttributeRefEnumValuesIgnoreCase);
	}

	private void doTest(boolean expectedValue, String theXsd) {
		boolean actualAccepted = true;
		try {
			validate(testXml, theXsd);
		} catch(SAXException e) {
			actualAccepted = false;
		} catch(IOException e) {
			fail(String.format("Got IOException: %s - %s", e.getMessage(), e.getStackTrace()));
		}
		assertEquals(expectedValue, actualAccepted);
	}

	private void validate(String testXml, String theXsd) throws SAXException, IOException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(new SAXSource(new InputSource(new StringReader(theXsd))));
		Validator validator = schema.newValidator();
		SAXSource source = new SAXSource(new InputSource(new StringReader(testXml)));
		validator.validate(source);
	}
}
