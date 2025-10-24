package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class TextConfigTest extends BaseIntegrationTest {

	private static final String START_CLASS_NAME = "org.frankframework.frankdoc.testtarget.textconfig.Start";

	@Test
	public void testStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			START_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "textconfig-expected.xsd");
	}

	@Test
	public void testCompatibilityXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			START_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "textconfig-expected-compatibility.xsd");
	}

	@Test
	public void testJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			START_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "textconfig-expected.json");
	}

	@Test
	public void testPluralStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			"org.frankframework.frankdoc.testtarget.textconfig.plural.Start"
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "textconfig-expected-strict-plural.xsd");
	}
}
