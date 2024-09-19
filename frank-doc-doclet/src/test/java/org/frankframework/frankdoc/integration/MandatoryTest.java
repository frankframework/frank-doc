package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class MandatoryTest extends BaseIntegrationTest {

	private final String MANDATORY_MULTIPLE_MASTER_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.mandatory.multiple.Master";
	private final String MANDATORY_SINGLE_MASTER_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.mandatory.single.Master";

	@Test
	public void testMultipleStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			MANDATORY_MULTIPLE_MASTER_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "mandatoryMultiple.xsd");
	}

	@Test
	public void testMultipleCompatibilityXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			MANDATORY_MULTIPLE_MASTER_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "mandatoryMultipleCompatibility.xsd");
	}

	@Test
	public void testMultipleJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			MANDATORY_MULTIPLE_MASTER_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "mandatoryMultiple.json");
	}

	@Test
	public void testSingleStrictXsd() throws Exception {
		var model = createModel(
			"singular-test-digester-rules.xml",
			null,
			MANDATORY_SINGLE_MASTER_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "mandatorySingle.xsd");
	}

	@Test
	public void testSingleJson() throws Exception {
		var model = createModel(
			"singular-test-digester-rules.xml",
			null,
			MANDATORY_SINGLE_MASTER_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "mandatorySingle.json");
	}
}
