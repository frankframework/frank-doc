package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class ReintroduceTest extends BaseIntegrationTest {

	private final String REINTRODUCE_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.reintroduce.Master";
	private final String MANDATORY_REINTRODUCES_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.making.mandatory.reintroduces.Master";

	@Test
	public void testStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			REINTRODUCE_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "reintroduce.xsd");
	}

	@Test
	public void testCompatibilityXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			REINTRODUCE_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "reintroduceCompatibility.xsd");
	}

	@Test
	public void testJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			REINTRODUCE_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "reintroduce.json");
	}

	@Test
	public void testMandatoryStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			MANDATORY_REINTRODUCES_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "makingMandatoryReintroduces.xsd");
	}

	@Test
	public void testMandatoryJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			MANDATORY_REINTRODUCES_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "makingMandatoryReintroduces.json");
	}
}
