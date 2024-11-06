package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class HierarchyTest extends BaseIntegrationTest {

	private static final String START_CLASS_NAME = "org.frankframework.frankdoc.testtarget.hierarchy.Start";
	private static final String DIGESTER_RULES_FILE = "hierarchy-digester-rules.xml";

	@Test
	public void testStrictXsd() throws Exception {
		var model = createModel(
			DIGESTER_RULES_FILE,
			null,
			START_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "hierarchy.xsd");
	}
}
