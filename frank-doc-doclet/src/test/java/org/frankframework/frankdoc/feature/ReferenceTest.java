package org.frankframework.frankdoc.feature;

import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ReferenceTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.featurepackage.";
	private static final String CLASS_NAME = PACKAGE + "Child";
	private static final String REF_TARGET = "RefTarget";

	private Reference instance;
	private FrankClass clazz;

	@BeforeEach
	public void setUp() throws Exception {
		FrankClassRepository classRepository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		instance = new Reference(classRepository);
		clazz = classRepository.findClass(CLASS_NAME);
	}

	private FrankMethod findMethod(String methodName) {
		return Arrays.asList(clazz.getDeclaredMethods()).stream()
				.filter(m -> m.getName().equals(methodName))
				.findFirst()
				.get();
	}

	@Test
	public void whenNoRefAnnotationThenNoReferencedMethod() {
		FrankMethod targetMethod = findMethod("notReferenced");
		assertNull(instance.valueOf(targetMethod));
	}

	@Test
	public void whenIbisDocRefThenReferenceFound() {
		FrankMethod targetMethod = findMethod("referencedByIbisDocRef");
		FrankMethod referenced = instance.valueOf(targetMethod);
		assertEquals(REF_TARGET, referenced.getDeclaringClass().getSimpleName());
		assertEquals("referencedByIbisDocRef", referenced.getName());
	}

	@Test
	public void whenIbisDocRefWithDummyOrderThenReferenceFound() {
		FrankMethod targetMethod = findMethod("referencedByIbisDocRefWithDummyOrder");
		FrankMethod referenced = instance.valueOf(targetMethod);
		assertEquals(REF_TARGET, referenced.getDeclaringClass().getSimpleName());
		assertEquals("referencedByIbisDocRefWithDummyOrder", referenced.getName());
	}

	@Test
	public void whenIbisDocRefWithMethodThenReferenceFound() {
		FrankMethod targetMethod = findMethod("referencedByIbisDocRefOtherMethod");
		FrankMethod referenced = instance.valueOf(targetMethod);
		assertEquals(REF_TARGET, referenced.getDeclaringClass().getSimpleName());
		assertEquals("otherMethod", referenced.getName());
	}

	@Test
	public void whenFfRefThenReferenceFound() {
		FrankMethod targetMethod = findMethod("referenceByFfRef");
		FrankMethod referenced = instance.valueOf(targetMethod);
		assertEquals(REF_TARGET, referenced.getDeclaringClass().getSimpleName());
		assertEquals("referenceByFfRef", referenced.getName());
	}
}
