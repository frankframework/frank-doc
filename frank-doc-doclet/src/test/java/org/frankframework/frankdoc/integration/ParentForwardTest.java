package org.frankframework.frankdoc.integration;

import org.junit.jupiter.api.Test;

/**
 * Tests that an abstract parent class with only @Forward annotations is correctly shown as the JSON parent,
 * rather than being skipped in favour of an ancestor with attributes.
 * <br/>
 * This is meant to recreate the structure we have in the framework: Base64Pipe extends abstract FixedForwardPipe (with @Forward on it)
 * extends AbstractPipe.
 */
class ParentForwardTest extends BaseIntegrationTest {

	@Test
	public void testJson() throws Exception {
		var model = createModel(
			GENERAL_DIGEST_RULES_FILE,
			null,
			"org.frankframework.frankdoc.testtarget.examples.parentforward.ConcretePipe"
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "parentforward.json");
	}
}
