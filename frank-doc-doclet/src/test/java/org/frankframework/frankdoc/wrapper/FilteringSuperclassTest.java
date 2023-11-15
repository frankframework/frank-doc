package org.frankframework.frankdoc.wrapper;

import org.frankframework.frankdoc.testdoclet.EasyDoclet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import javax.lang.model.element.Element;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class FilteringSuperclassTest {
	private static final String CHILD_PACKAGE = "org.frankframework.frankdoc.testtarget.doclet.filtering.second.";
	private static final String PARENT_PACKAGE = "org.frankframework.frankdoc.testtarget.doclet.filtering.first.";
	private static final String CHILD_CLASS = "ChildDerivedFromOtherPackageParent";
	private static final Set<String> RELEVANT_METHODS = new HashSet<>(Arrays.asList("setChild", "setParent"));

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		String[] keptMethods = RELEVANT_METHODS.stream().sorted().collect(Collectors.toList()).toArray(new String[] {});
		return Arrays.asList(new Object[][] {
			{"Omit superclass", true, PARENT_PACKAGE, "null", new String[] {"setChild"}},
			{"Keep superclass", false, CHILD_PACKAGE, "Parent", keptMethods}
		});
	}

	@Parameter(0)
	public String title;

	@Parameter(1)
	public boolean omitAllAsSuperclasses;

	@Parameter(2)
	public String superclassFilter;

	@Parameter(3)
	public String expectedSuperclassName;

	@Parameter(4)
	public String[] expectedMethodNames;

	private FrankClass childClass;

	@Before
	public void setUp() throws FrankDocException {
		List<String> packages = Arrays.asList(CHILD_PACKAGE, PARENT_PACKAGE);
		EasyDoclet easyDoclet = TestUtil.getEasyDoclet(CHILD_PACKAGE, PARENT_PACKAGE);
		Set<? extends Element> classDocs = TestUtil.getTypeElements(easyDoclet, null);
		FrankClassRepository repository = new FrankClassRepository(TestUtil.getDocTrees(easyDoclet), classDocs, new HashSet<>(packages), new HashSet<>(), new HashSet<>(Collections.singletonList(superclassFilter)));
		childClass = repository.findClass(CHILD_PACKAGE + CHILD_CLASS);
		assertNotNull(childClass);
	}

	@Test
	public void onlyWhenSuperclassNotExcludedThenSuperclassFound() {
		Optional<FrankClass> actualSuperclass = Optional.ofNullable(childClass.getSuperclass());
		String actualSuperclassName = actualSuperclass.map(FrankClass::getSimpleName).orElse("null");
		assertEquals(expectedSuperclassName, actualSuperclassName);
	}

	@Test
	public void onlyWhenSuperclassNotExcludedThenMethodInheritedFromSuperclassFound() {
		// There is no need to filter superclasses when filtering declared and inherited method.
		// Therefore, we omit this case from these tests.
		FrankMethod[] actualMethods = childClass.getDeclaredAndInheritedMethods();
		List<String> actualMethodNames = Arrays.stream(actualMethods)
				.map(FrankMethod::getName)
				.filter(RELEVANT_METHODS::contains)
				.sorted()
				.collect(Collectors.toList());
		assertArrayEquals(expectedMethodNames, actualMethodNames.toArray(new String[] {}));
	}
}
