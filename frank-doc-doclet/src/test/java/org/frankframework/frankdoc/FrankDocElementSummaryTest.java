package org.frankframework.frankdoc;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;

import org.junit.jupiter.api.Test;

import org.frankframework.frankdoc.integration.BaseIntegrationTest;
import org.frankframework.frankdoc.model.FrankDocModel;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;

class FrankDocElementSummaryTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.element.summary.";
	private static final String EXPECTED = String.format("              Master: Master%n"
		+ "              Object: %n"
		+ "    Other (from sub): %n"
		+ "Other (from summary): %n");

	@Test
	void testElementSummary() {
		FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE, FRANK_DOC_GROUP_VALUES_PACKAGE);
		FrankDocModel model = FrankDocModel.populate(getDigesterRulesURL(BaseIntegrationTest.GENERAL_DIGEST_RULES_FILE), null, PACKAGE + "Master", classRepository);
		FrankDocElementSummaryFactory instance = new FrankDocElementSummaryFactory(model);
		String actual = instance.getText();
		System.out.println(actual);
		assertEquals(EXPECTED, actual);
	}

	private URL getDigesterRulesURL(String fileName) {
		return TestUtil.resourceAsURL("doc/" + fileName);
	}

}
