package org.frankframework.frankdoc.wrapper;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class VariablesTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.wrapper.variables.";
	private static final String PACKAGE_ALT = "org.frankframework.frankdoc.testtarget.wrapper.variables.alt.";

	private FrankClassRepository repository;
	private FrankClass instance;

	@Before
	public void setUp() {
		repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE, PACKAGE_ALT);
	}

	@Test
	public void findValueInPublicStaticFinalIntField() throws Exception {
		instance = repository.findClass(PACKAGE + "Constants");
		assertEquals("7", instance.resolveValue("INT_CONSTANT"));
	}

	@Test
	public void findValueEnumConstant() throws Exception {
		instance = repository.findClass(PACKAGE + "Constants.MyEnum");
		assertEquals("ENUM_CONSTANT", instance.resolveValue("ENUM_CONSTANT"));
	}

	@Test
	public void findClassSamePackage() throws Exception {
		instance = repository.findClass(PACKAGE + "Constants");
		FrankClass result = instance.findClass("OtherClass");
		assertEquals(PACKAGE + "OtherClass", result.getName());
	}

	@Test
	public void findClassOutsidePackage() throws Exception {
		instance = repository.findClass(PACKAGE + "Constants");
		FrankClass result = instance.findClass(PACKAGE_ALT + "ClassInOtherPackage");
		assertEquals(PACKAGE_ALT + "ClassInOtherPackage", result.getName());
	}

	@Test
	public void findInnerClass() throws Exception {
		instance = repository.findClass(PACKAGE + "Constants");
		FrankClass result = instance.findClass("MyEnum");
		assertEquals(PACKAGE + "Constants.MyEnum", result.getName());
	}
}
