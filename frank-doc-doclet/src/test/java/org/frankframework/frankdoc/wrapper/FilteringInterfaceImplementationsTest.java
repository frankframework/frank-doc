package org.frankframework.frankdoc.wrapper;

import org.frankframework.frankdoc.testdoclet.EasyDoclet;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.lang.model.element.Element;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class FilteringInterfaceImplementationsTest {
	private static final String PREFIX = "org.frankframework.frankdoc.testtarget.doclet.filtering.";
	private static final String FIRST = "first.";
	private static final String SECOND = "second.";
	private static final String FIRST_PACKAGE = PREFIX + FIRST;
	private static final String SECOND_PACKAGE = PREFIX + SECOND;
	private static final String[] BOTH_PACKAGES = new String[] {FIRST_PACKAGE, SECOND_PACKAGE};
	private static final String FIRST_IMPL = "FirstImpl";
	private static final String CHILD_OF_FIRST_IMPL_IN_SECOND_PACKAGE = "ChildOfFirstImplInOtherPackage";
	private static final String SECOND_IMPL = "SecondImpl";
	private static final String THIRD_IMPL = "ThirdImpl";
	private static final String[] NO_EXCLUDES = new String[] {};
	private static final String[] THIRD_IMPL_EXCLUDED = new String[] {SECOND_PACKAGE + THIRD_IMPL};
	private static final String[] CHILD_OF_FIRST_IMPL_IN_SECOND_PACKAGE_EXCLUDED = new String[] {SECOND_PACKAGE + CHILD_OF_FIRST_IMPL_IN_SECOND_PACKAGE};

	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{"First package no exclude", List.of(FIRST_PACKAGE), asList(NO_EXCLUDES), new String[] {FIRST_IMPL}},
			{"First package with exclude", List.of(FIRST_PACKAGE), asList(THIRD_IMPL_EXCLUDED), new String[] {FIRST_IMPL}},
			{"Both packages no exclude", asList(BOTH_PACKAGES), asList(NO_EXCLUDES), new String[] {CHILD_OF_FIRST_IMPL_IN_SECOND_PACKAGE, FIRST_IMPL, SECOND_IMPL, THIRD_IMPL}},
			{"Both packages with exclude", asList(BOTH_PACKAGES), asList(THIRD_IMPL_EXCLUDED), new String[] {CHILD_OF_FIRST_IMPL_IN_SECOND_PACKAGE, FIRST_IMPL,SECOND_IMPL}},
			{"Both packages derived class of implementation excluded", asList(BOTH_PACKAGES), asList(CHILD_OF_FIRST_IMPL_IN_SECOND_PACKAGE_EXCLUDED), new String[] {FIRST_IMPL, SECOND_IMPL, THIRD_IMPL}}
		});
	}

	@MethodSource("data")
	@ParameterizedTest(name = "{0}, {1}")
	void test(String caseName, List<String> includes, List<String> excludes, String[] expectedImplementations) throws FrankDocException {
		Set<? extends Element> classDocs = TestUtil.getIncludedElements(BOTH_PACKAGES);
		FrankClassRepository repository = new FrankClassRepository(TestUtil.getDocTrees(), classDocs, new HashSet<>(includes), new HashSet<>(excludes), new HashSet<>());
		FrankClass clazz = repository.findClass(FIRST_PACKAGE + "MyInterface");
		List<FrankClass> implementations = clazz.getInterfaceImplementations();
		List<String> actualSimpleNames = implementations.stream()
			.map(FrankClass::getSimpleName)
			.sorted()
			.peek(System.out::println)
			.collect(Collectors.toList());
		assertArrayEquals(expectedImplementations, actualSimpleNames.toArray(new String[]{}));
	}

}
