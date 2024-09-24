package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class SimpleTest extends BaseIntegrationTest {

	private static final String START_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.simple.Start";
	private static final String DIGESTER_RULES_FILE = "examples-simple-digester-rules.xml";

	@Test
	public void testStrictXsd() throws Exception {
		var model = createModel(
			DIGESTER_RULES_FILE,
			null,
			START_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "simple.xsd");
	}

	@Test
	public void testCompatibilityXsd() throws Exception {
		var model = createModel(
			DIGESTER_RULES_FILE,
			null,
			START_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF_ENUM_VALUES_IGNORE_CASE);

		assertXsdEqual(actual, "simpleForCompatibility.xsd");
	}

	@Test
	public void testJson() throws Exception {
		var model = createModel(
			DIGESTER_RULES_FILE,
			"simple-appconstants.properties",
			START_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "simple.json");
	}
}
