package org.frankframework.frankdoc;

import org.frankframework.frankdoc.model.FrankDocModel;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FrankDocElementSummaryTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.element.summary.";
	private static final String DIGESTER_RULES_FILE_NAME = "general-test-digester-rules.xml";
	private static final String EXPECTED = Arrays.asList(
		"              Master: Master",
		"              Object: ",
		"    Other (from sub): ",
		"Other (from summary): ").stream().map(s -> s + "\n").collect(Collectors.joining());

	@Test
	public void testElementSummary() throws IOException {
		FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE, FRANK_DOC_GROUP_VALUES_PACKAGE);
		FrankDocModel model = FrankDocModel.populate(getDigesterRulesURL(DIGESTER_RULES_FILE_NAME), PACKAGE + "Master", classRepository);
		FrankDocElementSummaryFactory instance = new FrankDocElementSummaryFactory(model);
		String actual = instance.getText();
		System.out.println(actual);
		assertEquals(EXPECTED, actual);
	}

	private URL getDigesterRulesURL(String fileName) throws IOException {
		return TestUtil.resourceAsURL("doc/" + fileName);
	}

}
