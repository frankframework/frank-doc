package org.frankframework.frankdoc.integration;

import org.junit.jupiter.api.Test;

public class CategoryTest extends BaseIntegrationTest {

	@Test
	public void testJson() throws Exception {
		var model = createModel(
			"examples-category-digester-rules.xml",
			null,
			"org.frankframework.frankdoc.testtarget.category.Start"
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "category.json");
	}
}
