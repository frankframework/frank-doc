package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class ValueSubstitutionTest extends BaseIntegrationTest {

	private final String START_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.valueSubs.WithValueSubstitutions";

	@Test
	public void testStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			START_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "valueSubs.xsd");
	}

	@Test
	public void testJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			START_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "valueSubs.json");
	}
}
