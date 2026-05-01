package org.frankframework.frankdoc.integration;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.FrankDocXsdFactory;
import org.frankframework.frankdoc.FrankDocJsonFactory;
import org.frankframework.frankdoc.Utils;
import org.frankframework.frankdoc.XsdVersion;
import org.frankframework.frankdoc.model.FrankDocModel;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;

public abstract class BaseIntegrationTest {

	private static final String FRAMEWORK_VERSION = "1.2.3-SNAPSHOT";
	private static final Set<String> SKIPPABLE_CONTAINER_ELEMENTS = Set.of("Module", "Root");
	public static final String GENERAL_DIGEST_RULES_FILE = "general-test-digester-rules.xml";

	protected FrankDocModel createModel(String digesterRulesFileName, String appConstantsPropertiesFileName, String startClassName, String[] requiredPackages) throws IOException {
		FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(requiredPackages);
		URL appConstantsPropertiesUrl = getUrlFromFileName(appConstantsPropertiesFileName);
		return FrankDocModel.populate(getUrlFromFileName(digesterRulesFileName), appConstantsPropertiesUrl, startClassName, classRepository);
	}

	protected FrankDocModel createModel(String digesterRulesFileName, String appConstantsPropertiesFileName, String startClassName) throws Exception {
		int idx = startClassName.lastIndexOf(".");
		String originalPackage = startClassName.substring(0, idx);

		return createModel(digesterRulesFileName, appConstantsPropertiesFileName, startClassName, new String[] {originalPackage, FRANK_DOC_GROUP_VALUES_PACKAGE});
	}

	private URL getUrlFromFileName(String fileName) throws IOException {
		return TestUtil.resourceAsURL("doc/" + fileName);
	}

	protected String convertModelToJson(FrankDocModel model) {
		var factory = new FrankDocJsonFactory(model, FRAMEWORK_VERSION, SKIPPABLE_CONTAINER_ELEMENTS);
		return factory.getJson().toString();
	}

	protected void assertJsonEqual(String actual, String fileName) throws IOException, JSONException {
		System.out.println(Utils.jsonPretty(actual));
		String expectedJson = TestUtil.getTestFile("/doc/examplesExpected/" + fileName);
		JSONAssert.assertEquals(expectedJson, actual, false);
	}

	protected String convertModelToXsd(FrankDocModel model, XsdVersion version, AttributeTypeStrategy attributeTypeStrategy) {
		var factory = new FrankDocXsdFactory(model, attributeTypeStrategy, "1.2.3-SNAPSHOT", model.getRootClassName(), version);
		return factory.getSchema();
	}

	protected void assertXsdEqual(String actual, String fileName) throws IOException {
		String expectedXsd = TestUtil.getTestFile("/doc/examplesExpected/" + fileName);
		TestUtil.assertEqualsIgnoreCRLF("Comparing XSD: " + fileName, expectedXsd, actual);
	}

}
