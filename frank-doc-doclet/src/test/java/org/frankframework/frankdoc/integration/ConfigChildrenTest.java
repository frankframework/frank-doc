package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class ConfigChildrenTest extends BaseIntegrationTest {

	private final String CONFIG_CHILDREN_MASTER_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.config.children.Master";
	private final String CONFIG_CHILDREN_EXCLUDED_MASTER_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.config.children.excluded.Master";
	private final String CONFIG_CHILDREN2_MASTER_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.config.children2.Master";

	@Test
	public void testChildrenJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			CONFIG_CHILDREN_MASTER_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "configChildren.json");
	}

	@Test
	public void testChildren1StrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			CONFIG_CHILDREN_MASTER_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "configChildren.xsd");
	}

	@Test
	public void testChildren2StrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			CONFIG_CHILDREN2_MASTER_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "configChildren2.xsd");
	}

	@Test
	public void testExcludedJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			CONFIG_CHILDREN_EXCLUDED_MASTER_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "configChildrenExclude.json");
	}

	@Test
	public void testExcludedStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			CONFIG_CHILDREN_EXCLUDED_MASTER_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "configChildrenExclude.xsd");
	}

}
