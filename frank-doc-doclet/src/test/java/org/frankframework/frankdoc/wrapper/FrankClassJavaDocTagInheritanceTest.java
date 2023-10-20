package org.frankframework.frankdoc.wrapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class FrankClassJavaDocTagInheritanceTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.wrapper.inherit.javadoc.tag.";

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{"@ff.tagForChild", "ChildValue"},
			{"@ff.tagForParent", "ParentValue"},
			{"@ff.tagForChildInterface", "ChildInterfaceValue"},
			{"@ff.tagForParentInterface", "ParentInterfaceValue"}
		});
	}

	@Parameter(0)
	public String tagNameSearched;

	@Parameter(1)
	public String expectedTagValue;

	@Test
	public void testGetJavaDocTagIncludingInherited() throws Exception {
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		FrankClass instance = repository.findClass(PACKAGE + "Child");
		String actualTagValue = instance.getJavaDocTagIncludingInherited(tagNameSearched);
		assertEquals(expectedTagValue, actualTagValue);
	}
}
