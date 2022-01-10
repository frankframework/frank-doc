/* 
Copyright 2021 WeAreFrank! 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/
package org.frankframework.frankdoc;

import static org.junit.Assume.assumeNotNull;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import javax.json.JsonObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.frankframework.frankdoc.model.FrankDocModel;

@RunWith(Parameterized.class)
public class DocWriterNewAndJsonGenerationExamplesTest {
	@Parameters(name = "{0}-{1}-{4}-{5}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "examples-simple-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.simple.Start", "simple.xsd", "simple.json"},
			{XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF_ENUM_VALUES_IGNORE_CASE, "examples-simple-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.simple.Start", "simpleForCompatibility.xsd", "simple.json"},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "examples-sequence-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.sequence.Master", "sequence.xsd", "sequence.json"},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.config.children.Master", "configChildren.xsd", "configChildren.json"},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.config.children2.Master", "configChildren2.xsd", null},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "examples-simple-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.deprecated.Master", null, "deprecated.json"},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.deprecated.enumValue.Master", "deprecatedEnumValueStrict.xsd", "deprecatedEnumValue.json"},
			{XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF_ENUM_VALUES_IGNORE_CASE, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.deprecated.enumValue.Master", "deprecatedEnumValueCompatibility.xsd", null},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.compatibility.fortype.Start", "compatibility-test-expected-strict.xsd", null},
			{XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.compatibility.fortype.Start", "compatibility-test-expected-compatibility.xsd", null},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.config.child.no.i.Master", "configChildNoI.xsd", null},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.compatibility.multiple.Start", "compatibility-multiple-test-expected-strict.xsd", null},
			{XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.compatibility.multiple.Start", "compatibility-multiple-test-expected-compatibility.xsd", null},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.textconfig.Start", "textconfig-expected.xsd", "textconfig-expected.json"},
			{XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.textconfig.Start", "textconfig-expected-compatibility.xsd", null},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.textconfig.plural.Start", "textconfig-expected-strict-plural.xsd", null},
			// Tests that GroupCreator considers parent elements that only reject attributes, not introduce them.
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.exclude.Master", "exclude.xsd", "exclude.json"},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.type.defaultElement.Master", "withDefault.xsd", null},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.highest.commonInterface.Master", "highestCommonInterface.xsd", "highestCommonInterface.json"},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.parameters.Master", null, "parameters.json"},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.plural.config.defaultClassname.Master", "testPluralConflictDefaultOption.xsd", null},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "multiword-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.pattern.violation.A", "testDigesterRulesViolations-strict.xsd", "testDigesterRulesViolations.json"},
			{XsdVersion.COMPATIBILITY, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "multiword-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.pattern.violation.A", "testDigesterRulesViolations-compatibility.xsd", null},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "multiword-digester-rules-root.xml", "org.frankframework.frankdoc.testtarget.examples.pattern.violation.root.A", null, "testDigesterRulesViolationsRoot.json"},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "multiword-digester-rules-long.xml", "org.frankframework.frankdoc.testtarget.examples.pattern.violation.longPattern.A", null, "testDigesterRulesViolationsLong.json"},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "reduced-digester-rules.xml", "org.frankframework.frankdoc.testtarget.entity.reference.Configuration", "entityReference.xsd", "entityReference.json"},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.element.name.Master", null, "elementNames.json"},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.attributeDefault.Master", "attributeDefault.xsd", null},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.sameEnum.only.once.Master", "enumOnlyOnce.xsd", null},
			// Also tests tags.
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.examples.forwards.Master", null, "forwards.json"},
			{XsdVersion.STRICT, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "general-test-digester-rules.xml", "org.frankframework.frankdoc.testtarget.parent.without.attributes.Master", "handleParentOnlyExcludedAttributes.xsd", null}
		});
	}

	@Parameter(0)
	public XsdVersion xsdVersion;

	@Parameter(1)
	public AttributeTypeStrategy attributeTypeStrategy;

	@Parameter(2)
	public String digesterRulesFileName;

	@Parameter(3)
	public String startClassName;

	@Parameter(4)
	public String expectedXsdFileName;

	@Parameter(5)
	public String expectedJsonFileName;

	private String packageOfClasses;

	@Before
	public void setUp() {
		int idx = startClassName.lastIndexOf(".");
		packageOfClasses = startClassName.substring(0, idx);
	}

	@Test
	public void testXsd() throws Exception {
		assumeNotNull(expectedXsdFileName);
		FrankDocModel model = createModel();
		DocWriterNew docWriter = new DocWriterNew(model, attributeTypeStrategy);
		docWriter.init(startClassName, xsdVersion);
		String actualXsd = docWriter.getSchema();
		System.out.println(actualXsd);
		String expectedXsd = TestUtil.getTestFile("/doc/examplesExpected/" + expectedXsdFileName);
		TestUtil.assertEqualsIgnoreCRLF(expectedXsd, actualXsd);
	}

	private FrankDocModel createModel() throws Exception {
		FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(packageOfClasses);
		return FrankDocModel.populate(getDigesterRulesURL(digesterRulesFileName), startClassName, classRepository);
	}

	private URL getDigesterRulesURL(String fileName) throws IOException {
		return TestUtil.resourceAsURL("doc/" + fileName);
	}

	@Test
	public void testJson() throws Exception {
		assumeNotNull(expectedJsonFileName);
		FrankDocModel model = createModel();
		FrankDocJsonFactory jsonFactory = new FrankDocJsonFactory(model);
		JsonObject jsonObject = jsonFactory.getJson();
		String actual = jsonObject.toString();
		System.out.println(Utils.jsonPretty(actual));
		String expectedJson = TestUtil.getTestFile("/doc/examplesExpected/" + expectedJsonFileName);
		TestUtil.assertJsonEqual("Comparing JSON", expectedJson, actual);
	}
}
