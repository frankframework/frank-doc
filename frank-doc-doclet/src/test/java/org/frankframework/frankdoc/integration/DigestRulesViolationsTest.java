package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.XsdVersion;
import org.junit.jupiter.api.Test;

public class DigestRulesViolationsTest extends BaseIntegrationTest {

	private final String VIOLATION_A_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.pattern.violation.A";
	private final String VIOLATION_ROOT_A_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.pattern.violation.root.A";
	private final String VIOLATION_LONG_PATTERN_A_CLASS_NAME = "org.frankframework.frankdoc.testtarget.examples.pattern.violation.longPattern.A";

	@Test
	public void testStrictXsd() throws Exception {
		var model = createModel(
			"multiword-digester-rules.xml",
			null,
			VIOLATION_A_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "testDigesterRulesViolations-strict.xsd");
	}

	@Test
	public void testCompatibilityXsd() throws Exception {
		var model = createModel(
			"multiword-digester-rules.xml",
			null,
			VIOLATION_A_CLASS_NAME
		);
		var actual = convertModelToXsd(model, XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF);

		assertXsdEqual(actual, "testDigesterRulesViolations-compatibility.xsd");
	}

	@Test
	public void testJson() throws Exception {
		var model = createModel(
			"multiword-digester-rules.xml",
			null,
			VIOLATION_A_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "testDigesterRulesViolations.json");
	}

	@Test
	public void testRootJson() throws Exception {
		var model = createModel(
			"multiword-digester-rules-root.xml",
			null,
			VIOLATION_ROOT_A_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "testDigesterRulesViolationsRoot.json");
	}

	@Test
	public void testLongPatternJson() throws Exception {
		var model = createModel(
			"multiword-digester-rules-long.xml",
			null,
			VIOLATION_LONG_PATTERN_A_CLASS_NAME
		);
		var actual = convertModelToJson(model);

		assertJsonEqual(actual, "testDigesterRulesViolationsLong.json");
	}
}
