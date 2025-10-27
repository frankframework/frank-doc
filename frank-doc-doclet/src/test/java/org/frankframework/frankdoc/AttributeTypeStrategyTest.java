package org.frankframework.frankdoc;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.addComplexType;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.addElementWithName;
import static org.frankframework.frankdoc.FrankDocXsdFactoryXmlUtils.getXmlSchema;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.frankframework.frankdoc.model.AttributeEnum;
import org.frankframework.frankdoc.model.AttributeType;
import org.frankframework.frankdoc.model.ElementChild;
import org.frankframework.frankdoc.model.FrankAttribute;
import org.frankframework.frankdoc.model.FrankDocModel;
import org.frankframework.frankdoc.util.XmlBuilder;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AttributeTypeStrategyTest {
	private String schemaStringAllowAttributeRef;
	private String schemaStringAllowAttributeRefEnumValuesIgnoreCase;

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
	public String testXml;

	@BeforeEach
	public void setUp() throws IOException {
		String packageOfEnum = "org.frankframework.frankdoc.testtarget.attribute.type.strategy.";
		FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(packageOfEnum, FRANK_DOC_GROUP_VALUES_PACKAGE);
		String digesterRulesFileName = "doc/empty-digester-rules.xml";
		FrankDocModel model = FrankDocModel.populate(TestUtil.resourceAsURL(digesterRulesFileName), null, packageOfEnum + "Container", classRepository);
		FrankAttribute attribute = model.findFrankElement(packageOfEnum + "Container").getAttributes(ElementChild.ALL_NOT_EXCLUDED).get(0);
		AttributeEnum attributeEnum = model.findAttributeEnum(packageOfEnum + "Container.TestType");
		schemaStringAllowAttributeRef = getXsd(AttributeTypeStrategy.ALLOW_PROPERTY_REF, attributeEnum, attribute);
		schemaStringAllowAttributeRefEnumValuesIgnoreCase = getXsd(AttributeTypeStrategy.ALLOW_PROPERTY_REF_ENUM_VALUES_IGNORE_CASE, attributeEnum, attribute);
	}

	private static String getXsd(AttributeTypeStrategy attributeTypeStrategy, AttributeEnum attributeEnum, FrankAttribute enumTypedAttribute) {
		XmlBuilder schema = getXmlSchema("1.2.3-SNAPSHOT");
		XmlBuilder element = addElementWithName(schema, "myElement");
		XmlBuilder complexType = addComplexType(element);
		// We do not test mandatory attributes here. Therefore the fourth argument is "false".
		complexType.addSubElement(attributeTypeStrategy.createAttribute("boolAttr", AttributeType.BOOL));
		complexType.addSubElement(attributeTypeStrategy.createAttribute("intAttr", AttributeType.INT));
		complexType.addSubElement(attributeTypeStrategy.createAttribute("stringAttr", AttributeType.STRING));
		complexType.addSubElement(AttributeTypeStrategy.createAttributeActive());
		// This test does not test whether use="required" is included. It is about
		// attribute types. Therefore we take the attribute not to be mandatory.
		complexType.addSubElement(attributeTypeStrategy.createRestrictedAttribute(enumTypedAttribute, a -> {}));
		attributeTypeStrategy.createHelperTypes().forEach(schema::addSubElement);
		schema.addSubElement(attributeTypeStrategy.createAttributeEnumType(attributeEnum));
		return schema.toXML(true);
	}

	@MethodSource("data")
	@ParameterizedTest(name = "{0}")
	void testAllowPropertyRef(String title, String testXml, boolean allowPropertyRefShouldAccept, boolean allowPropertyRefEnumValuesIgnoreCaseShouldAccept) {
		this.testXml = testXml;
		doTest(allowPropertyRefShouldAccept, schemaStringAllowAttributeRef);
	}

	@MethodSource("data")
	@ParameterizedTest(name = "{0}")
	void testAllowPropertyRefEnumValuesIgnoreCase(String title, String testXml, boolean allowPropertyRefShouldAccept, boolean allowPropertyRefEnumValuesIgnoreCaseShouldAccept) {
		this.testXml = testXml;
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
