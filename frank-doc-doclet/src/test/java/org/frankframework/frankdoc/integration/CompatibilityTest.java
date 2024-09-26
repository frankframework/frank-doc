package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class CompatibilityTest extends BaseIntegrationTest {

	private final String FOR_TYPE_START_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.compatibility.fortype.Start";
	private final String MULTIPLE_START_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.compatibility.multiple.Start";
	private final String IGNORE_START_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.ignore.in.compatibility.Master";


	@Test
	public void testForTypeStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			FOR_TYPE_START_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "compatibility-test-expected-strict.xsd");
	}

	@Test
	public void testForTypeCompatibilityXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			FOR_TYPE_START_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "compatibility-test-expected-compatibility.xsd");
	}

	@Test
	public void testMultipleStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			MULTIPLE_START_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "compatibility-multiple-test-expected-strict.xsd");
	}

	@Test
	public void testMultipleCompatibilityXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			MULTIPLE_START_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "compatibility-multiple-test-expected-compatibility.xsd");
	}

	@Test
	public void testIgnoreStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			IGNORE_START_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "ignoreInCompatibilityStrict.xsd");
	}

	@Test
	public void testIgnoreCompatibilityXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			IGNORE_START_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "ignoreInCompatibilityCompatibility.xsd");
	}

	@Test
	public void testIgnoreJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			IGNORE_START_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "ignoreInCompatibility.json");
	}
}
