package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class ExcludeTest extends BaseIntegrationTest {

	private final String EXCLUDE_MASTER_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.exclude.Master";

	@Test
	public void testStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			EXCLUDE_MASTER_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "exclude.xsd");
	}

	@Test
	public void testJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			EXCLUDE_MASTER_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "exclude.json");
	}
}
