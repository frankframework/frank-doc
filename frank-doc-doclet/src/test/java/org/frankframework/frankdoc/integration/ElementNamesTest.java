package org.frankframework.frankdoc.integration;

import org.junit.jupiter.api.Test;

public class ElementNamesTest extends BaseIntegrationTest {

	@Test
	public void testJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			"org.frankframework.frankdoc.testtarget.element.name.Master"
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "elementNames.json");
	}
}
