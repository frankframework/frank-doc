package org.frankframework.frankdoc.integration;

import org.junit.jupiter.api.Test;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;

public class ServletAuthenticatorTest extends BaseIntegrationTest {

	private final static String SERVLET_PROVIDER_PACKAGE = "org.frankframework.lifecycle.servlets.";
	private final static String START_PACKAGE = "org.frankframework.frankdoc.testtarget.dummy.";
	private final static String START_CLASS_NAME = START_PACKAGE + "Dummy";


	@Test
	public void test() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			START_CLASS_NAME,
			new String[] {START_PACKAGE, FRANK_DOC_GROUP_VALUES_PACKAGE, SERVLET_PROVIDER_PACKAGE}
		);

		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "servletAuthenticator.json");
	}

}
