package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class EntityReferenceTest extends BaseIntegrationTest {

	private final String ENTITY_REFERENCE_CONFIGURATION_CLASS_NAME = "org.frankframework.frankdoc.testtarget.entity.reference.Configuration";

	@Test
	public void testStrictXsd() throws Exception {
		var model = createModel(
			"reduced-digester-rules.xml",
			null,
			ENTITY_REFERENCE_CONFIGURATION_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "entityReference.xsd");
	}

	@Test
	public void testJson() throws Exception {
		var model = createModel(
			"reduced-digester-rules.xml",
			null,
			ENTITY_REFERENCE_CONFIGURATION_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "entityReference.json");
	}
}
