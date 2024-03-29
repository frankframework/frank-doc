package org.frankframework.frankdoc.feature;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankEnumConstant;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractNonValuedFeatureTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.featurepackage.";
	private FrankClassRepository classes;
	private FrankClass forMethods;
	private FrankClass deprecatedByAnnotation;
	private FrankClass deprecatedByTag;
	private FrankClass myEnum;

	@BeforeEach
	public void setUp() throws Exception {
		classes = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		forMethods = classes.findClass(PACKAGE + "ForMethods");
		deprecatedByAnnotation = classes.findClass(PACKAGE + "DeprecatedByAnnotation");
		deprecatedByTag = classes.findClass(PACKAGE + "DeprecatedByTag");
		myEnum = classes.findClass(PACKAGE + "MyEnum");
		assertTrue(myEnum.isEnum());
	}

	@Test
	public void testForMethods() {
		assertTrue(Optional.getInstance().isSetOn(findMethod(forMethods, "withOptionalWithAnnotation")));
		assertTrue(Optional.getInstance().isSetOn(findMethod(forMethods, "withOptionalByTag")));
		assertFalse(Optional.getInstance().isSetOn(findMethod(forMethods, "withoutOptional")));
	}

	@Test
	public void testForClass() {
		assertTrue(Deprecated.getInstance().isSetOn(deprecatedByAnnotation));
		assertTrue(Deprecated.getInstance().isSetOn(deprecatedByTag));
		assertFalse(Deprecated.getInstance().isSetOn(forMethods));
	}

	@Test
	public void testForEnum() {
		FrankEnumConstant[] constants = myEnum.getEnumConstants();
		assertTrue(Deprecated.getInstance().isSetOn(constants[0]));
		assertTrue(Deprecated.getInstance().isSetOn(constants[1]));
		assertFalse(Deprecated.getInstance().isSetOn(constants[2]));
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
