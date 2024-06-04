package org.frankframework.frankdoc.wrapper;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FrankMethodTest extends TestBase {
	@Test
	public void testMethod() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "Parent");
		FrankMethod setter = TestUtil.getDeclaredMethodOf(clazz, "setInherited");
		assertEquals("setInherited", setter.getName());
		assertTrue(setter.isPublic());
		FrankAnnotation[] annotations = setter.getAnnotations();
		assertEquals(1, annotations.length);
		FrankAnnotation annotation = annotations[0];
		assertEquals(FrankDocletConstants.IBISDOC, annotation.getName());
		annotation = setter.getAnnotation(FrankDocletConstants.IBISDOC);
		assertNotNull(annotation);
		assertEquals(FrankDocletConstants.IBISDOC, annotation.getName());
		FrankType returnType = setter.getReturnType();
		assertNotNull(returnType);
		assertTrue(returnType.isPrimitive());
		assertEquals("void", returnType.getName());
		assertEquals(4, setter.getParameterCount());

		FrankType[] parameters = setter.getParameterTypes();
		assertEquals(4, parameters.length);
		FrankType parameter1 = parameters[0];
		assertFalse(parameter1.isPrimitive());
		assertEquals(FrankDocletConstants.STRING, parameter1.getName());
		FrankType parameter2 = parameters[1];
		assertTrue(parameter2 instanceof FrankClass);

		assertTrue(parameters[2] instanceof FrankClass);
		assertTrue(parameters[3] instanceof FrankClass);

		annotation = setter.getAnnotationIncludingInherited(FrankDocletConstants.IBISDOC);
		assertNotNull(annotation);
		assertEquals(FrankDocletConstants.IBISDOC, annotation.getName());
	}

	@Test
	public void whenMethodIsPackagePrivateThenNotFound() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "Child");
		FrankMethod method = TestUtil.getDeclaredMethodOf(clazz, "packagePrivateMethod");
		assertNull(method);
	}

	@Test
	public void whenNoAnnotationsThenNullReturned() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "Child");
		FrankMethod method = TestUtil.getDeclaredMethodOf(clazz, "methodWithoutAnnotations");
		assertEquals(0, method.getAnnotations().length);
		assertNull(method.getAnnotation(FrankDocletConstants.IBISDOC));
	}

	@Test
	public void whenInheritedAnnotationsRequestedThenInheritedAnnotationsIncluded() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "Child");
		FrankMethod method = TestUtil.getDeclaredMethodOf(clazz, "setInherited");
		assertNotNull(method);
		FrankAnnotation annotation = method.getAnnotation(FrankDocletConstants.IBISDOC);
		assertNull(annotation);
		annotation = method.getAnnotationIncludingInherited(FrankDocletConstants.IBISDOC);
		assertNotNull(annotation);
		assertEquals(FrankDocletConstants.IBISDOC, annotation.getName());
	}

	@Test
	public void overriddenMethodHasOverriderAsDeclaringClass() throws Exception {
		FrankClass clazz = classRepository.findClass(PACKAGE + "Child");
		FrankMethod setter = getMethodFromDeclaredAndInheritedMethods(clazz, "setInherited");
		assertEquals("Child", setter.getDeclaringClass().getSimpleName());
	}

	@Test
	public void inheritedMethodHasAncestorAsDeclaringClass() throws Exception{
		FrankClass clazz = classRepository.findClass(PACKAGE + "Child");
		FrankMethod getter = getMethodFromDeclaredAndInheritedMethods(clazz, "getInherited");
		assertEquals("Parent", getter.getDeclaringClass().getSimpleName());
	}

	private FrankMethod getMethodFromDeclaredAndInheritedMethods(FrankClass clazz, String methodName) {
		FrankMethod[] methods = clazz.getDeclaredAndInheritedMethods();
		List<FrankMethod> getters = Arrays.asList(methods).stream()
				.filter(m -> m.getName().equals(methodName))
				.toList();
		assertEquals(1, getters.size());
		return getters.get(0);
	}

	@Test
	public void whenMethodHasStringVarargsThenIsVarargs() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "Child");
		FrankMethod method = TestUtil.getDeclaredMethodOf(clazz, "setVarargStringMethod");
		assertTrue(method.isVarargs());
		assertEquals(1, method.getParameterCount());
		FrankType parameter = method.getParameterTypes()[0];
		// We use the parameter type to check whether we have a setter or not.
		// If a method has a vararg argument, then it is not a setter.
		// In this case the exact type of the parameter is not needed.
		Set<String> stringOrStringArray = new HashSet<>(Arrays.asList(FrankDocletConstants.STRING, FrankDocletConstants.STRING + "[]"));
		assertTrue(stringOrStringArray.contains(parameter.getName()));
	}

	@Test
	public void whenMethodHasEnumVarargsThenIsVarargs() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "Child");
		FrankMethod method = TestUtil.getDeclaredMethodOf(clazz, "setVarargEnumMethod");
		assertTrue(method.isVarargs());
		assertEquals(1, method.getParameterCount());
		FrankType parameter = method.getParameterTypes()[0];
		assertEquals("org.frankframework.frankdoc.testtarget.doclet.MyEnum", parameter.getName());
	}

	@Test
	public void whenMethodDoesNotHaveVarargsThenNotVarargs() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "Child");
		FrankMethod method = TestUtil.getDeclaredMethodOf(clazz, "setInherited");
		assertFalse(method.isVarargs());
		assertEquals(4, method.getParameterCount());
		FrankType parameter = method.getParameterTypes()[0];
		assertEquals(FrankDocletConstants.STRING, parameter.getName());
	}

	@Test
	public void annotationCanBeInheritedFromImplementedInterface() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "Child");
		FrankMethod method = TestUtil.getDeclaredMethodOf(clazz, "myAnnotatedMethod");
		FrankAnnotation annotation = method.getAnnotationIncludingInherited(TestUtil.DEPRECATED);
		assertNotNull(annotation);
		assertEquals(TestUtil.DEPRECATED, annotation.getName());
	}

	@Test
	public void testToString() throws Exception {
		FrankClass clazz = classRepository.findClass(PACKAGE + "Child");
		FrankMethod method = TestUtil.getDeclaredMethodOf(clazz, "myAnnotatedMethod");
		assertEquals("Child.myAnnotatedMethod", method.toString());
	}
}
