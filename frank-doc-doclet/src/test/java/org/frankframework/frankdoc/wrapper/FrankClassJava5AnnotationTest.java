package org.frankframework.frankdoc.wrapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

import static org.frankframework.frankdoc.wrapper.TestUtil.JAVADOC_GROUP_ANNOTATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FrankClassJava5AnnotationTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.doclet.interfaces.java5.annotation.";

	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{"ClassWithJava5Annotation", "ClassGroup"},
			{"ClassWithoutJava5Annotation", null},
			{"InheriterFromParent", "ClassGroup"},
			{"ParentOverrider", "ParentOverriderGroup"},
			{"InheriterFromChildInterface", "InterfaceGroup"},
			{"InheriterFromGrandparent", "ClassGroup"}
		});
	}
	public String queriedClass;
	public String expectedValue;

	@MethodSource("data")
	@ParameterizedTest(name = "{0}")
	public void testJavaAnnotationValue(String queriedClass, String expectedValue) throws Exception {
		initFrankClassJava5AnnotationTest(queriedClass, expectedValue);
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		FrankClass instance = repository.findClass(PACKAGE + queriedClass);
		// TODO: Will rename this method and give it Java annotation class name as argument.
		// This has to be done for similar methods in FrankMethod as well.
		FrankAnnotation actualGroupAnnotation = instance.getAnnotationIncludingInherited(JAVADOC_GROUP_ANNOTATION);
		if(expectedValue == null) {
			assertNull(actualGroupAnnotation);
		} else {
			assertEquals(expectedValue, (String) actualGroupAnnotation.getValueOf("name"));
		}
	}

	public void initFrankClassJava5AnnotationTest(String queriedClass, String expectedValue) {
		this.queriedClass = queriedClass;
		this.expectedValue = expectedValue;
	}
}
