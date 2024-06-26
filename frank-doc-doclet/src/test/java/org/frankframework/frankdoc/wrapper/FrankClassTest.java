package org.frankframework.frankdoc.wrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FrankClassTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.doclet.";
	private static final String ANNOTATION_WITH_CLASS_ARRAY = "org.frankframework.doc.ExcludeFromType";

	private static final String EXPECTED_TYPE_ELEMENT =
			"This is test class \"Child\". We use this comment to see how\n" +
			" JavaDoc text is treated by the Doclet API.";

	private FrankClassRepository repository;
	private FrankClass instance;

	@BeforeEach
	public void setUp() throws FrankDocException {
		repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		instance = repository.findClass(PACKAGE + "Child");
	}

	@Test
	public void testGetJavaDoc() {
		assertEquals(EXPECTED_TYPE_ELEMENT, instance.getJavaDoc());
	}

	@Test
	public void testMethodSequenceIsPreserved() {
		List<String> actualMethodNames = Arrays.asList(instance.getDeclaredMethods()).stream()
				.map(FrankMethod::getName)
				.toList();
		assertEquals(7, actualMethodNames.size());
		String[] expectedMethodNames = new String[] {"setInherited", "setVarargStringMethod", "setVarargEnumMethod", "getMyInnerEnum", "myAnnotatedMethod", "methodWithoutAnnotations", "getProtectedStuff"};
		assertArrayEquals(expectedMethodNames, actualMethodNames.toArray(new String[] {}));
	}

	@Test
	public void testExtendsOrInherits() throws Exception {
		FrankClass grandChild = repository.findClass(PACKAGE + "GrandChild");
		FrankClass child = repository.findClass(PACKAGE + "Child");
		FrankClass parent = repository.findClass(PACKAGE + "Parent");
		FrankClass myInterface = repository.findClass(PACKAGE + "MyInterface");
		assertTrue(grandChild.extendsOrImplements(parent));
		assertTrue(grandChild.extendsOrImplements(myInterface));
		assertFalse(myInterface.extendsOrImplements(child));
	}

	@Test
	public void testAnnotationHavingClassArrayValue() throws Exception {
		FrankClass clazz = repository.findClass(PACKAGE + "WithAnnotationHavingClassArray");
		FrankAnnotation annotation = clazz.getAnnotation(ANNOTATION_WITH_CLASS_ARRAY);
		String[] value = (String[]) annotation.getValue();
		assertArrayEquals(new String[] {PACKAGE + "Child", PACKAGE + "GrandChild"}, value);
	}

	@Test
	public void testAnnotationHavingClassArrayValueOneValue() throws Exception {
		FrankClass clazz = repository.findClass(PACKAGE + "WithAnnotationHavingClassArrayOneValue");
		FrankAnnotation annotation = clazz.getAnnotation(ANNOTATION_WITH_CLASS_ARRAY);
		String[] value = (String[]) annotation.getValue();
		assertArrayEquals(new String[] {PACKAGE + "Child"}, value);
	}

	@Test
	public void testParentIsTopLevel() throws FrankDocException {
		FrankClass clazz = repository.findClass(PACKAGE + "Parent");
		assertNotNull(clazz);
		assertTrue(clazz.isTopLevel());
	}

	@Test
	public void testStaticInnerClassIsNotTopLevel() throws FrankDocException {
		FrankClass clazz = repository.findClass(PACKAGE + "Child.ResponseValidatorWrapper");
		assertNotNull(clazz);
		assertFalse(clazz.isTopLevel());
	}

	@Test
	public void testInnerEnumIsNotTopLevel() throws FrankDocException {
		FrankClass clazz = repository.findClass(PACKAGE + "Child.MyInnerEnum");
		assertNotNull(clazz);
		assertFalse(clazz.isTopLevel());
	}
}
