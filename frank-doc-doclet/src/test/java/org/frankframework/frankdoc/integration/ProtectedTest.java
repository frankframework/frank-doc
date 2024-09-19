package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class ProtectedTest extends BaseIntegrationTest {

	private final String OMIT_CONFIG_CHILD_PROTECTED_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.omit.config.childProtected.Master";

	@Test
	public void testStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			OMIT_CONFIG_CHILD_PROTECTED_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "omitConfigChildWhenClassProtected.xsd");
	}

	@Test
	public void testJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			OMIT_CONFIG_CHILD_PROTECTED_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "omitConfigChildWhenClassProtected.json");
	}
}