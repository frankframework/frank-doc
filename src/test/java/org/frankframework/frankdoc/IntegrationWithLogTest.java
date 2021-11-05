package org.frankframework.frankdoc;

import java.net.URL;

import org.frankframework.frankdoc.model.FrankDocModel;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.Test;

public class IntegrationWithLogTest {
	@Test
	public void whenPluralConfigChildHasMultipleCandidatesForDefaultElementThenWarning() throws Exception {
		TestAppender appender = TestAppender.newBuilder().build();
		TestAppender.addToRootLogger(appender);
		try {
			String thePackage = "org.frankframework.frankdoc.testtarget.plural.config.defaultClassname.";
			String startClassName = thePackage + "Master";
			FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(thePackage);
			URL digesterRulesUrl = TestUtil.resourceAsURL("doc/general-test-digester-rules.xml");
			FrankDocModel model = FrankDocModel.populate(digesterRulesUrl, startClassName, classRepository);
			DocWriterNew docWriter = new DocWriterNew(model, AttributeTypeStrategy.ALLOW_PROPERTY_REF);
			docWriter.init(startClassName, XsdVersion.STRICT);
			String actualXsd = docWriter.getSchema();
			String expectedXsd = TestUtil.getTestFile("/doc/examplesExpected/testPluralConflictDefaultOption.xsd");
			TestUtil.assertEqualsIgnoreCRLF(expectedXsd, actualXsd);
			appender.assertLogged("ConfigChildSet [ConfigChildSet(ObjectConfigChild(MyElement.registerB(IChild1)), ObjectConfigChild(MyElement.registerB(IChild2)))] has multiple candidates for the default element: [org.frankframework.frankdoc.testtarget.plural.config.defaultClassname.Child1, org.frankframework.frankdoc.testtarget.plural.config.defaultClassname.Child2]");
			appender.assertLogged("ConfigChildSet [ConfigChildSet(ObjectConfigChild(Master.registerA(IInterface1)), ObjectConfigChild(Master.registerA(IInterface2)))] has multiple candidates for the default element: [org.frankframework.frankdoc.testtarget.plural.config.defaultClassname.Interface1, org.frankframework.frankdoc.testtarget.plural.config.defaultClassname.Interface2]");
		} finally {
			TestAppender.removeAppender(appender);
		}
	}

	@Test
	public void testGroupCreatorCanHandleClassWithoutAttributesForJson() {
		
	}

}
