package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class AttributesTest extends BaseIntegrationTest {

	@Test
	public void testReuseStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			"org.frankframework.frankdoc.testtarget.examples.reuse.attributes.Master"
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "reuseAttributes.xsd");
	}

	@Test
	public void testNoReuseStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			"org.frankframework.frankdoc.testtarget.examples.no.reuse.attributes.overloaded.Master"
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "noReuseAttributesOverloaded.xsd");
	}
}
