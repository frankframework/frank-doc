package org.frankframework.frankdoc.wrapper;

import org.frankframework.frankdoc.model.FrankElement;
import org.frankframework.frankdoc.testtarget.doclet.ClassValuedAnnotation;
import org.frankframework.frankdoc.testtarget.doclet.Java5Annotation;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FrankAnnotationTest extends TestBase {

	@Test
	public void whenArrayAnnotaionValueProvidedAsArrayThenFetchable() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "DeprecatedChild");
		FrankMethod setter = TestUtil.getDeclaredMethodOf(clazz, "someSetter");
		assertEquals("someSetter", setter.getName());
	}

	@Test
	public void whenAnnotationHasFieldNotNamedValueThenStillReadable() throws Exception {
		FrankClass clazz = classRepository.findClass(PACKAGE + "Parent");
		FrankAnnotation annotation = clazz.getAnnotation(Java5Annotation.class.getName());
		assertNotNull(annotation);
		Object stringArrayRawValue = annotation.getValueOf("myStringArray");
		String[] stringArrayValue = (String[]) stringArrayRawValue;
		assertArrayEquals(new String[]{"first", "second"}, stringArrayValue);
	}

	@Test
	public void whenAnnotationHasStringFieldThenReadable() throws Exception {
		FrankClass clazz = classRepository.findClass(PACKAGE + "Parent");
		FrankAnnotation annotation = clazz.getAnnotation(Java5Annotation.class.getName());
		assertNotNull(annotation);
		Object stringRawValue = annotation.getValueOf("myString");
		String stringValue = (String) stringRawValue;
		assertEquals("A string", stringValue);
	}

	@Test
	public void whenAnnotationHasBooleanFieldThenReadable() throws Exception {
		FrankClass clazz = classRepository.findClass(PACKAGE + "Parent");
		FrankAnnotation annotation = clazz.getAnnotation(Java5Annotation.class.getName());
		assertNotNull(annotation);
		Object booleanRawValue = annotation.getValueOf("myBoolean");
		Boolean booleanValue = (Boolean) booleanRawValue;
		assertTrue(booleanValue);
	}

	@Test
	public void whenClassValuedAnnotationOnClassThenValueObtainedAsString() throws Exception {
		FrankClass clazz = classRepository.findClass(PACKAGE + "Parent");
		FrankAnnotation classAnnotation = clazz.getAnnotation(ClassValuedAnnotation.class.getName());
		assertEquals("org.frankframework.frankdoc.testtarget.doclet.Parent", classAnnotation.getValue());
	}

	@Test
	public void whenClassValuedAnnotationOnMethodThenValueObtainedAsString() throws Exception {
		FrankClass clazz = classRepository.findClass(PACKAGE + "Parent");
		FrankMethod method = TestUtil.getDeclaredMethodOf(clazz, "withClassValuedAnnotation");
		FrankAnnotation classAnnotation = method.getAnnotation(ClassValuedAnnotation.class.getName());
		assertEquals("org.frankframework.frankdoc.testtarget.doclet.Parent", classAnnotation.getValue());
	}

	@Test
	public void testAnnotationsOfAnnotations() {
		classRepository = TestUtil.getFrankClassRepositoryDoclet("org.frankframework.frankdoc.testtarget.examples.labels");
		FrankClass clazz = classRepository.findMatchingClass("Master");
		FrankAnnotation[] annotations = clazz.getAnnotations();

		assertEquals(3, annotations.length);
		Arrays.stream(annotations).forEach(frankAnnotation -> assertEquals(1, frankAnnotation.getAnnotationCount()));

		List<FrankAnnotation> annotationsForLabels = Arrays.stream(annotations)
			.filter(a -> a.getAnnotation(FrankElement.JAVADOC_LABEL_ANNOTATION_CLASSNAME) != null)
			.collect(Collectors.toList());

		assertEquals(3, annotationsForLabels.size());
	}
}
