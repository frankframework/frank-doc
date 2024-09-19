package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class HighestCommonInterfaceTest extends BaseIntegrationTest {

	private final String HIGHEST_COMMON_INTERFACE_MASTER_CLASS_NAME = "org.frankframework.frankdoc.testtarget.highest.commonInterface.Master";

	@Test
	public void testStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			HIGHEST_COMMON_INTERFACE_MASTER_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "highestCommonInterface.xsd");
	}

	@Test
	public void testJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			HIGHEST_COMMON_INTERFACE_MASTER_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "highestCommonInterface.json");
	}
}
