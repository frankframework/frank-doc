package org.frankframework.frankdoc;

import org.frankframework.frankdoc.model.FrankDocModel;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Arrays;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IntegrationWithLogTest {
	@Test
	public void whenPluralConfigChildHasMultipleCandidatesForDefaultElementThenLogged() throws Exception {
		TestAppender appender = TestAppender.newBuilder().build();
		TestAppender.addToRootLogger(appender);
		try {
			String thePackage = "org.frankframework.frankdoc.testtarget.plural.config.defaultClassname.";
			String startClassName = thePackage + "Master";
			FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(thePackage, FRANK_DOC_GROUP_VALUES_PACKAGE);
			URL digesterRulesUrl = TestUtil.resourceAsURL("doc/general-test-digester-rules.xml");
			FrankDocModel model = FrankDocModel.populate(digesterRulesUrl, null, startClassName, classRepository);
			DocWriterNew docWriter = new DocWriterNew(model, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "1.2.3-SNAPSHOT");
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
	public void whenAttributeOverloadedWithTypeConflictThenLogged() throws Exception {
		TestAppender appender = TestAppender.newBuilder().build();
		TestAppender.addToRootLogger(appender);
		try {
			String thePackage = "org.frankframework.frankdoc.testtarget.attribute.overload.";
			String startClassName = thePackage + "Master";
			FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(thePackage, FRANK_DOC_GROUP_VALUES_PACKAGE);
			URL digesterRulesUrl = TestUtil.resourceAsURL("doc/general-test-digester-rules.xml");
			FrankDocModel.populate(digesterRulesUrl, null, startClassName, classRepository);

			// Verify that the overloaded methods are found
			FrankClass masterClass = classRepository.findClass("org.frankframework.frankdoc.testtarget.attribute.overload.Master");
			FrankMethod[] methods = masterClass.getDeclaredAndInheritedMethods();
			FrankMethod parentOverloadedMethod = Arrays.stream(methods).filter(m -> m.toString().equals("Parent.setOverloadedInherited")).findFirst().orElse(null);
			assertNotNull(parentOverloadedMethod);
			assertEquals(17, masterClass.getDeclaredAndInheritedMethods().length);

			appender.assertLogged("Class [org.frankframework.frankdoc.testtarget.attribute.overload.Master] has overloaded declared or inherited attribute setters. Type of attribute [overloadedInherited] can be any of [int, java.lang.String]");
			appender.assertLogged("Class [org.frankframework.frankdoc.testtarget.attribute.overload.Master] has overloaded declared or inherited attribute setters. Type of attribute [overloadedEnum] can be any of [java.lang.String, org.frankframework.frankdoc.testtarget.attribute.overload.MyEnum]");
			appender.assertLogged("In Frank element [Master]: setter [setMyAttribute] has type [org.frankframework.frankdoc.testtarget.attribute.overload.MyEnum] while the getter has type [java.lang.String]");
			appender.assertLogged("In Frank element [Master]: setter [setMyAttribute2] has type [java.lang.String] while the getter has type [org.frankframework.frankdoc.testtarget.attribute.overload.MyEnum]");
		} finally {
			TestAppender.removeAppender(appender);
		}
	}

	@Test
	public void testContradictionsWithDefaultAndMandatoryAreLogged() throws Exception {
		TestAppender appender = TestAppender.newBuilder().build();
		TestAppender.addToRootLogger(appender);
		try {
			String thePackage = "org.frankframework.frankdoc.testtarget.attributeDefault.";
			String startClassName = thePackage + "Master";
			FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(thePackage, FRANK_DOC_GROUP_VALUES_PACKAGE);
			URL digesterRulesUrl = TestUtil.resourceAsURL("doc/general-test-digester-rules.xml");
			FrankDocModel model = FrankDocModel.populate(digesterRulesUrl, null, startClassName, classRepository);
			DocWriterNew docWriter = new DocWriterNew(model, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "1.2.3-SNAPSHOT");
			docWriter.init(startClassName, XsdVersion.STRICT);
			String actualXsd = docWriter.getSchema();
			String expectedXsd = TestUtil.getTestFile("/doc/examplesExpected/attributeDefault.xsd");
			TestUtil.assertEqualsIgnoreCRLF(expectedXsd, actualXsd);
			appender.assertLogged("Attribute [Master.explicitNullOnPrimitive] is of primitive type [short] but has default value null");
			appender.assertLogged("Attribute [Master.mandatoryWithDefault] is [MANDATORY], but it also has a default value: [something]");
		} finally {
			TestAppender.removeAppender(appender);
		}
	}

	@Test
	public void whenParameterOrForwardLacksDescriptionThenLogged() throws Exception {
		TestAppender appender = TestAppender.newBuilder().build();
		TestAppender.addToRootLogger(appender);
		try {
			String thePackage = "org.frankframework.frankdoc.testtarget.examples.parameters.forwards.warnings.";
			String startClassName = thePackage + "Master";
			FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(thePackage, FRANK_DOC_GROUP_VALUES_PACKAGE);
			URL digesterRulesUrl = TestUtil.resourceAsURL("doc/general-test-digester-rules.xml");
			FrankDocModel.populate(digesterRulesUrl, null, startClassName, classRepository);
			appender.assertLogged("FrankElement [org.frankframework.frankdoc.testtarget.examples.parameters.forwards.warnings.Master] has a [@ff.parameter] tag without a value: [myParamWithoutDescription]");
			appender.assertLogged("FrankElement [org.frankframework.frankdoc.testtarget.examples.parameters.forwards.warnings.Master] has a [@ff.forward] tag without a value: [myForwardWithoutDescription]");
		} finally {
			TestAppender.removeAppender(appender);
		}
	}

	public void whenFfTagOccursMultipleTimesWithSameNameThenError() throws Exception {
		TestAppender appender = TestAppender.newBuilder().build();
		TestAppender.addToRootLogger(appender);
		try {
			String thePackage = "org.frankframework.frankdoc.testtarget.tag.not.unique.";
			String startClassName = thePackage + "Master";
			FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(thePackage, FRANK_DOC_GROUP_VALUES_PACKAGE);
			URL digesterRulesUrl = TestUtil.resourceAsURL("doc/general-test-digester-rules.xml");
			FrankDocModel.populate(digesterRulesUrl, null, startClassName, classRepository);
			appender.assertLogged("FrankElement [org.frankframework.frankdoc.testtarget.tag.not.unique.Master] has multiple values for tag [myTag]");
		} finally {
			TestAppender.removeAppender(appender);
		}
	}

	@Test
	public void whenSpecificParameterHasNoNameAndNoDescriptionThenLogged() throws Exception {
		TestAppender appender = TestAppender.newBuilder().build();
		TestAppender.addToRootLogger(appender);
		try {
			String thePackage = "org.frankframework.frankdoc.testtarget.tag.no.name.";
			String startClassName = thePackage + "Master";
			FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(thePackage, FRANK_DOC_GROUP_VALUES_PACKAGE);
			URL digesterRulesUrl = TestUtil.resourceAsURL("doc/general-test-digester-rules.xml");
			FrankDocModel.populate(digesterRulesUrl, null, startClassName, classRepository);
			appender.assertLogged("Error parsing a [@ff.parameter] tag of class [org.frankframework.frankdoc.testtarget.tag.no.name.Master]");
		} finally {
			TestAppender.removeAppender(appender);
		}
	}
}
