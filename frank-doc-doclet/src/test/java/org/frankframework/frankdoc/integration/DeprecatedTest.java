package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class DeprecatedTest extends BaseIntegrationTest {

	private final String DEPRECATED_MASTER_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.deprecated.Master";
	private final String DEPRECATED_ENUM_VALUE_MASTER_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.deprecated.enumValue.Master";

	@Test
	public void testJson() throws Exception {
		var model = createModel(
			"examples-simple-digester-rules.xml",
			null,
			DEPRECATED_MASTER_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "deprecated.json");
	}

	@Test
	public void testEnumStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			DEPRECATED_ENUM_VALUE_MASTER_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "deprecatedEnumValueStrict.xsd");
	}

	@Test
	public void testEnumCompatibilityXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			DEPRECATED_ENUM_VALUE_MASTER_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF_ENUM_VALUES_IGNORE_CASE);

		assertXsdEqual(actual, "deprecatedEnumValueCompatibility.xsd");
	}

	@Test
	public void testEnumJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			DEPRECATED_ENUM_VALUE_MASTER_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "deprecatedEnumValue.json");
	}
}
