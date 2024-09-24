package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;

public class NameConflictTest extends BaseIntegrationTest {

	private final String NAME_CONFLICT_FIRST_MASTER_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.simple.name.conflict.first.Master";
	private final String[] REQUIRED_PACKAGES = new String[] {
		"org.frankframework.frankdoc.testtarget.examples.simple.name.conflict.first",
		"org.frankframework.frankdoc.testtarget.examples.simple.name.conflict.second",
		FRANK_DOC_GROUP_VALUES_PACKAGE
	};

	@Test
	public void testStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			NAME_CONFLICT_FIRST_MASTER_CLASS_NAME,
			REQUIRED_PACKAGES
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "nameConflictStrict.xsd");
	}

	@Test
	public void testCompatibilityXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			NAME_CONFLICT_FIRST_MASTER_CLASS_NAME,
			REQUIRED_PACKAGES
		);
		var actual = convertModelToXsd(model, XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "nameConflictCompatibility.xsd");
	}

	@Test
	public void testJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			NAME_CONFLICT_FIRST_MASTER_CLASS_NAME,
			REQUIRED_PACKAGES
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "nameConflict.json");
	}
}
