package org.frankframework.frankdoc.integration;

import org.junit.jupiter.api.Test;

class WrapperLabelTest extends BaseIntegrationTest {

	@Test
	void testWrapperPipeIsClassifiedAsWrapper() throws Exception {
		var model = createModel(
			"wrapper-label-test-digester-rules.xml",
			null,
			"org.frankframework.frankdoc.testtarget.examples.wrapper.Master"
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "wrapperLabels.json");
	}
}
