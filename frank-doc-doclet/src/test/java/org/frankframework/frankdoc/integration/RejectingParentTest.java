package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class RejectingParentTest extends BaseIntegrationTest {

	@Test
	public void testStrictXsd() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			"org.frankframework.frankdoc.testtarget.examples.rejecting.group.defined.Master"
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "dontForgetRejectingParents.xsd");
	}
}