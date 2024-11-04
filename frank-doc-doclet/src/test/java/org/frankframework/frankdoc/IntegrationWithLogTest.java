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
		try (TestAppender appender = TestAppender.newBuilder().build()) {
			String thePackage = "org.frankframework.frankdoc.testtarget.plural.config.defaultClassname.";
			String startClassName = thePackage + "Master";
			FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(thePackage, FRANK_DOC_GROUP_VALUES_PACKAGE);
			URL digesterRulesUrl = TestUtil.resourceAsURL("doc/general-test-digester-rules.xml");
			FrankDocModel model = FrankDocModel.populate(digesterRulesUrl, null, startClassName, classRepository);
			FrankDocXsdFactory factory = new FrankDocXsdFactory(model, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "1.2.3-SNAPSHOT", startClassName, XsdVersion.STRICT);
			String actualXsd = factory.getSchema();
			String expectedXsd = TestUtil.getTestFile("/doc/examplesExpected/testPluralConflictDefaultOption.xsd");
			TestUtil.assertEqualsIgnoreCRLF(expectedXsd, actualXsd);
			appender.assertLogged("ConfigChildSet [ConfigChildSet(ObjectConfigChild(MyElement.addB(IChild1)), ObjectConfigChild(MyElement.addB(IChild2)))] has multiple candidates for the default element: [org.frankframework.frankdoc.testtarget.plural.config.defaultClassname.Child1, org.frankframework.frankdoc.testtarget.plural.config.defaultClassname.Child2]");
			appender.assertLogged("ConfigChildSet [ConfigChildSet(ObjectConfigChild(Master.addA(IInterface1)), ObjectConfigChild(Master.addA(IInterface2)))] has multiple candidates for the default element: [org.frankframework.frankdoc.testtarget.plural.config.defaultClassname.Interface1, org.frankframework.frankdoc.testtarget.plural.config.defaultClassname.Interface2]");
		}
	}

	@Test
	public void whenAttributeOverloadedWithTypeConflictThenLogged() throws Exception {
		try (TestAppender appender = TestAppender.newBuilder().build()) {
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
		}
	}

	@Test
	public void testContradictionsWithDefaultAndMandatoryAreLogged() throws Exception {
		try (TestAppender appender = TestAppender.newBuilder().build()) {
			String thePackage = "org.frankframework.frankdoc.testtarget.attributeDefault.";
			String startClassName = thePackage + "Master";
			FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(thePackage, FRANK_DOC_GROUP_VALUES_PACKAGE);
			URL digesterRulesUrl = TestUtil.resourceAsURL("doc/general-test-digester-rules.xml");
			FrankDocModel model = FrankDocModel.populate(digesterRulesUrl, null, startClassName, classRepository);
			FrankDocXsdFactory factory = new FrankDocXsdFactory(model, AttributeTypeStrategy.ALLOW_PROPERTY_REF, "1.2.3-SNAPSHOT", startClassName, XsdVersion.STRICT);
			String actualXsd = factory.getSchema();
			String expectedXsd = TestUtil.getTestFile("/doc/examplesExpected/attributeDefault.xsd");
			TestUtil.assertEqualsIgnoreCRLF(expectedXsd, actualXsd);
			appender.assertLogged("Attribute [Master.mandatoryWithDefault] is [MANDATORY], but it also has a default value: [something]");
		}
	}

	@Test
	public void whenParameterOrForwardLacksDescriptionThenLogged() throws Exception {
		try (TestAppender appender = TestAppender.newBuilder().build()) {
			String thePackage = "org.frankframework.frankdoc.testtarget.examples.parameters.forwards.warnings.";
			String startClassName = thePackage + "Master";
			FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(thePackage, FRANK_DOC_GROUP_VALUES_PACKAGE);
			URL digesterRulesUrl = TestUtil.resourceAsURL("doc/general-test-digester-rules.xml");
			FrankDocModel.populate(digesterRulesUrl, null, startClassName, classRepository);
			appender.assertLogged("FrankElement [org.frankframework.frankdoc.testtarget.examples.parameters.forwards.warnings.Master] has a [@ff.parameter] tag without a value: [myParamWithoutDescription]");
		}
	}
}
