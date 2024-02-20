package org.frankframework.frankdoc.wrapper;

import org.frankframework.frankdoc.testdoclet.EasyDoclet;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.lang.model.element.Element;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FilteringSuperclassTest {
	private static final String CHILD_PACKAGE = "org.frankframework.frankdoc.testtarget.doclet.filtering.second.";
	private static final String PARENT_PACKAGE = "org.frankframework.frankdoc.testtarget.doclet.filtering.first.";
	private static final String CHILD_CLASS = "ChildDerivedFromOtherPackageParent";
	private static final Set<String> RELEVANT_METHODS = new HashSet<>(Arrays.asList("setChild", "setParent"));

	public static Collection<Object[]> data() {
		String[] keptMethods = RELEVANT_METHODS.stream().sorted().collect(Collectors.toList()).toArray(new String[] {});
		return Arrays.asList(new Object[][] {
			{"Omit superclass", PARENT_PACKAGE, "null", new String[] {"setChild"}},
			{"Keep superclass", CHILD_PACKAGE, "Parent", keptMethods}
		});
	}

	private FrankClass childClass;

	public void setUp(String superclassFilter) throws FrankDocException {
		List<String> packages = Arrays.asList(CHILD_PACKAGE, PARENT_PACKAGE);
		EasyDoclet easyDoclet = TestUtil.getEasyDoclet(CHILD_PACKAGE, PARENT_PACKAGE);
		Set<? extends Element> classDocs = TestUtil.getTypeElements(easyDoclet, null);
		FrankClassRepository repository = new FrankClassRepository(TestUtil.getDocTrees(easyDoclet), classDocs, new HashSet<>(packages), new HashSet<>(), new HashSet<>(Collections.singletonList(superclassFilter)));
		childClass = repository.findClass(CHILD_PACKAGE + CHILD_CLASS);
		assertNotNull(childClass);
	}

	@MethodSource("data")
	@ParameterizedTest(name = "{0}")
	public void onlyWhenSuperclassNotExcludedThenSuperclassFound(String title, String superclassFilter, String expectedSuperclassName, String[] expectedMethodNames) throws Exception {
		setUp(superclassFilter);
		Optional<FrankClass> actualSuperclass = Optional.ofNullable(childClass.getSuperclass());
		String actualSuperclassName = actualSuperclass.map(FrankClass::getSimpleName).orElse("null");
		assertEquals(expectedSuperclassName, actualSuperclassName);
	}

	@MethodSource("data")
	@ParameterizedTest(name = "{0}")
	public void onlyWhenSuperclassNotExcludedThenMethodInheritedFromSuperclassFound(String title, String superclassFilter, String expectedSuperclassName, String[] expectedMethodNames) throws Exception {
		setUp(superclassFilter);
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
