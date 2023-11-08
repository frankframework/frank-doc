package org.frankframework.frankdoc.wrapper;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FrankMethodOverrideTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.packageprivate.override.";
	private FrankClassRepository repository;

	@Before
	public void setUp() {
		repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
	}

	@Test
	public void whenPackagePrivateOverriddenByPublicThenOnlyChildMethodConsidered() throws FrankDocException {
		FrankClass child = repository.findClass(PACKAGE + "Child");
		FrankMethod childMethod = getMethodByName(child, "setAlarm");
		String javaDoc = childMethod.getJavaDocIncludingInherited();
		// We test here that the JavaDoc above AbstractParent.setAlarm() is ignored. That
		// method is package-private.
		assertNull(javaDoc);
	}

	@Test
	public void testOverriddenMethodShouldInheritJavadoc() throws FrankDocException {
		FrankClass child = repository.findClass(PACKAGE + "Child");
		FrankMethod childMethod = getMethodByName(child, "setDestinationName");
		String javaDoc = childMethod.getJavaDocIncludingInherited();
		assertEquals("Name of the destination (queue or topic) to use", javaDoc);
	}

	@Test
	public void testVarShouldInheritJavadoc() throws FrankDocException {
		FrankClass child = repository.findClass(PACKAGE + "Child");
		FrankMethod setKeyField = getMethodByName(child, "setKeyField");
		assertEquals("Primary key field of the table, used to identify messages. <a href=\"https://www.eclipse.org/paho/files/javadoc\" target=\"_blank\">link</a>.\n use <code>XML</code> shizzle.", setKeyField.getJavaDocIncludingInherited());
	}

	@Test
	public void testVarShouldInheritDefaultJavadoc() throws FrankDocException {
		FrankClass child = repository.findClass(PACKAGE + "Child");
		FrankMethod setForceMethod = getMethodByName(child, "setForceMessageIdAsCorrelationId");

		assertEquals("By default text from Parent.", setForceMethod.getJavaDocIncludingInherited());
		assertEquals("Text from the Default-tag in Child.", setForceMethod.getAnnotation("nl.nn.adapterframework.doc.Default").getValue());
		assertEquals("FrankAnnotationDoclet name: [nl.nn.adapterframework.doc.Default], value: [Text from the Default-tag in Child.]", setForceMethod.getAnnotations()[1].toString());
	}

	private FrankMethod getMethodByName(FrankClass c, String name) {
		return Arrays.stream(c.getDeclaredAndInheritedMethods())
			.filter(m -> m.getName().equals(name))
			.collect(Collectors.toList()).get(0);
	}
}
