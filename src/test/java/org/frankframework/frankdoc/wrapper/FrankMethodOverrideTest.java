package org.frankframework.frankdoc.wrapper;

import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public class FrankMethodOverrideTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.packageprivate.override.";
	private FrankClassRepository repository;

	@Before
	public void setUp() {
		repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
	}

	@Test
	public void whenPackagePrivateOverriddenByPublicThenOnlyChildMethodConsidered() throws Exception {
		FrankClass child = repository.findClass(PACKAGE + "Child");
		FrankMethod childMethod = getMethodByName(child, "setAlarm");
		String javaDoc = childMethod.getJavaDocIncludingInherited();
		// We test here that the JavaDoc above AbstractParent.setAlarm() is ignored. That
		// method is package-private.
		assertNull(javaDoc);
	}

	private FrankMethod getMethodByName(FrankClass c, String name) {
		return Arrays.asList(c.getDeclaredMethods()).stream()
				.filter(m -> m.getName().equals(name))
				.collect(Collectors.toList()).get(0);
	}
}
