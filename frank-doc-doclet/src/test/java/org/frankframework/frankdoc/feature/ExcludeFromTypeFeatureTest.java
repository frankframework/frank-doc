package org.frankframework.frankdoc.feature;

import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExcludeFromTypeFeatureTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.featurepackage.";

	private FrankClassRepository classes;

	@BeforeEach
	public void setUp() throws Exception {
		classes = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
	}

	@Test
	public void testValueFromAnnotation() throws Exception {
		check(classes.findClass(PACKAGE + "WithExcludeFromTypeAsAnnotation"));
	}

	private void check(FrankClass clazz) throws Exception {
		Set<FrankClass> result = ExcludeFromTypeFeature.getInstance(classes).excludedFrom(clazz);
		assertEquals(2, result.size());
		assertTrue(result.contains(classes.findClass(PACKAGE + "Parent")));
		assertTrue(result.contains(classes.findClass(PACKAGE + "Child")));
	}

	@Test
	public void testValueFromTag() throws Exception {
		check(classes.findClass(PACKAGE + "WithExcludeFromTypeAsTag"));
	}

	@Test
	public void testValueFromAnnotationThatReferencesOnlyOneClass() throws Exception {
		FrankClass clazz = classes.findClass(PACKAGE + "WithExcludeFromTypeAsAnnotationSingleValue");
		Set<FrankClass> result = ExcludeFromTypeFeature.getInstance(classes).excludedFrom(clazz);
		assertEquals(1, result.size());
		assertTrue(result.contains(classes.findClass(PACKAGE + "Parent")));
	}
}
