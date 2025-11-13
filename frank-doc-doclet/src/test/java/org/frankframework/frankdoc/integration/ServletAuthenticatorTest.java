package org.frankframework.frankdoc.integration;

import org.junit.jupiter.api.Test;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;

class ServletAuthenticatorTest extends BaseIntegrationTest {

	private static final String SERVLET_PROVIDER_PACKAGE = "org.frankframework.lifecycle.servlets.";
	private static final String START_PACKAGE = "org.frankframework.frankdoc.testtarget.dummy.";
	private static final String START_CLASS_NAME = START_PACKAGE + "Dummy";

	@Test
	void test() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			START_CLASS_NAME,
			new String[] {START_PACKAGE, FRANK_DOC_GROUP_VALUES_PACKAGE, SERVLET_PROVIDER_PACKAGE}
		);

		assertJsonEqual(convertModelToJson(model), "servletAuthenticator.json");
	}
}
