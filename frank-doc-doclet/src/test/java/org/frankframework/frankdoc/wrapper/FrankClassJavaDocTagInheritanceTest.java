package org.frankframework.frankdoc.wrapper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FrankClassJavaDocTagInheritanceTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.wrapper.inherit.javadoc.tag.";

	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{"@ff.tagForChild", "ChildValue"},
			{"@ff.tagForParent", "ParentValue"},
			{"@ff.tagForChildInterface", "ChildInterfaceValue"},
			{"@ff.tagForParentInterface", "ParentInterfaceValue"}
		});
	}

	@MethodSource("data")
	@ParameterizedTest(name = "{0}")
	void testGetJavaDocTagIncludingInherited(String tagNameSearched, String expectedTagValue) throws Exception {
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		FrankClass instance = repository.findClass(PACKAGE + "Child");
		String actualTagValue = instance.getJavaDocTagIncludingInherited(tagNameSearched);
		assertEquals(expectedTagValue, actualTagValue);
	}

}
