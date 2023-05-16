package org.frankframework.frankdoc.feature;

import static org.junit.Assert.assertArrayEquals;

import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.Before;
import org.junit.Test;

public class ExcludeFromTypeFeatureTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.featurepackage.";

	private FrankClassRepository classes;

	@Before
	public void setUp() throws Exception {
		classes = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
	}

	@Test
	public void testValueFromAnnotation() throws Exception {
		FrankClass clazz = classes.findClass(PACKAGE + "WithExcludeFromTypeAsAnnotation");
		assertArrayEquals(new String[] {PACKAGE + "Parent", PACKAGE + "Child"}, ExcludeFromTypeFeature.getInstance().excludedFrom(clazz));
	}

	@Test
	public void testValueFromTag() throws Exception {
		FrankClass clazz = classes.findClass(PACKAGE + "WithExcludeFromTypeAsTag");
		assertArrayEquals(new String[] {PACKAGE + "Parent", PACKAGE + "Child"}, ExcludeFromTypeFeature.getInstance().excludedFrom(clazz));
	}
}
