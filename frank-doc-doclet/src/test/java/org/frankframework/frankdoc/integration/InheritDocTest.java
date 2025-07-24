package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class InheritDocTest extends BaseIntegrationTest {

	private final String INHERIT_DOC_CHILD_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.inheritdoc.Child";

	@Test
	public void testStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			INHERIT_DOC_CHILD_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "inheritdoc.xsd");
	}

	@Test
	public void testJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			INHERIT_DOC_CHILD_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "inheritdoc.json");
	}

	@Test
	public void testIgnoredJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			"org.frankframework.frankdoc.testtarget.examples.inheritdoc.Ignored"
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "inheritdoc.json");
	}
}
