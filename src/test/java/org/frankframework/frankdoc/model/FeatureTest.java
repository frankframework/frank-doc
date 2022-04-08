package org.frankframework.frankdoc.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankEnumConstant;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.Before;
import org.junit.Test;

public class FeatureTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.feature.";
	private FrankClassRepository classes;
	private FrankClass forMethods;
	private FrankClass forInheritanceFromMethods;
	private FrankClass deprecatedByAnnotation;
	private FrankClass deprecatedByTag;
	private FrankClass myEnum;

	@Before
	public void setUp() throws Exception {
		classes = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		forMethods = classes.findClass(PACKAGE + "ForMethods");
		forInheritanceFromMethods = classes.findClass(PACKAGE + "ForInheritanceFromMethods");
		deprecatedByAnnotation = classes.findClass(PACKAGE + "DeprecatedByAnnotation");
		deprecatedByTag = classes.findClass(PACKAGE + "DeprecatedByTag");
		myEnum = classes.findClass(PACKAGE + "MyEnum");
		assertTrue(myEnum.isEnum());
	}

	@Test
	public void whenMethodHasTheJavaAnnotationThenHasFeature() throws Exception {
		FrankMethod method = findMethod(forMethods, "withDefaultWithAnnotation");
		FrankMethod inheritedMethod = findMethod(forInheritanceFromMethods, "withDefaultWithAnnotation");
		assertTrue(Feature.DEFAULT.isSetOn(method));
		assertTrue(Feature.DEFAULT.isEffectivelySetOn(inheritedMethod));
		assertEquals("myDefault", Feature.DEFAULT.valueOf(method));
		assertEquals("myDefault", Feature.DEFAULT.valueOf(inheritedMethod));
	}

	@Test
	public void whenMethodHasTheTagThenHasFeature() throws Exception {
		FrankMethod method = findMethod(forMethods, "withDefaultByTag");
		FrankMethod inheritedMethod = findMethod(forInheritanceFromMethods, "withDefaultByTag");
		assertTrue(Feature.DEFAULT.isSetOn(method));
		assertTrue(Feature.DEFAULT.isEffectivelySetOn(inheritedMethod));
		assertEquals("myDefault", Feature.DEFAULT.valueOf(method));
		assertEquals("myDefault", Feature.DEFAULT.valueOf(inheritedMethod));
	}

	@Test
	public void whenMethodDoesNotHaveTheFeatureThenFeatureNotAssigned() throws Exception {
		FrankMethod method = findMethod(forMethods, "withoutDefault");
		FrankMethod inheritedMethod = findMethod(forInheritanceFromMethods, "withoutDefault");
		assertFalse(Feature.DEFAULT.isSetOn(method));
		assertFalse(Feature.DEFAULT.isEffectivelySetOn(inheritedMethod));
		assertNull(Feature.DEFAULT.valueOf(method));
		assertNull(Feature.DEFAULT.valueOf(inheritedMethod));
	}

	@Test
	public void testForClass() {
		assertTrue(Feature.DEPRECATED.isSetOn(deprecatedByAnnotation));
		assertTrue(Feature.DEPRECATED.isSetOn(deprecatedByTag));
		assertFalse(Feature.DEPRECATED.isSetOn(forMethods));
	}

	@Test
	public void testForEnum() throws Exception {
		FrankEnumConstant[] constants = myEnum.getEnumConstants();
		assertTrue(Feature.DEPRECATED.isSetOn(constants[0]));
		assertTrue(Feature.DEPRECATED.isSetOn(constants[1]));
		assertFalse(Feature.DEPRECATED.isSetOn(constants[2]));
	}

	@Test
	public void whenMethodIsNotMandatoryThenNoFeatureMandatory() throws Exception {
		FrankMethod method = findMethod(forMethods, "notMandatory");
		FrankMethod inheritedMethod = findMethod(forInheritanceFromMethods, "notMandatory");
		assertFalse(Feature.MANDATORY.isSetOn(method));
		assertFalse(Feature.MANDATORY.isEffectivelySetOn(inheritedMethod));
		assertNull(Feature.MANDATORY.valueOf(method));
		assertNull(Feature.MANDATORY.valueOf(inheritedMethod));
	}

	@Test
	public void whenMethodHasMandatoryJavaAnnotationThenHasFeatureMandatory() throws Exception {
		FrankMethod method = findMethod(forMethods, "mandatoryByAnnotation");
		FrankMethod inheritedMethod = findMethod(forInheritanceFromMethods, "mandatoryByAnnotation");
		assertTrue(Feature.MANDATORY.isSetOn(method));
		assertTrue(Feature.MANDATORY.isEffectivelySetOn(inheritedMethod));
		assertEquals("true", Feature.MANDATORY.valueOf(method));
		assertEquals("true", Feature.MANDATORY.valueOf(inheritedMethod));
	}

	@Test
	public void whenMethodHasMandatoryTagThenHasFeatureMandatory() throws Exception {
		FrankMethod method = findMethod(forMethods, "mandatoryByTag");
		FrankMethod inheritedMethod = findMethod(forInheritanceFromMethods, "mandatoryByTag");
		assertTrue(Feature.MANDATORY.isSetOn(method));
		assertTrue(Feature.MANDATORY.isEffectivelySetOn(inheritedMethod));
		assertEquals("ignoreInCompatibilityMode", Feature.MANDATORY.valueOf(method));
		assertEquals("ignoreInCompatibilityMode", Feature.MANDATORY.valueOf(inheritedMethod));
	}

	private FrankMethod findMethod(FrankClass clazz, String methodName) {
		for(FrankMethod m: clazz.getDeclaredMethods()) {
			if(m.getName().equals(methodName)) {
				return m;
			}
		}
		return null;
	}
}
