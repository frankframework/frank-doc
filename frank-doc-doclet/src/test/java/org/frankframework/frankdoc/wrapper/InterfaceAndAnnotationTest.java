package org.frankframework.frankdoc.wrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InterfaceAndAnnotationTest {
	static final String PACKAGE = "org.frankframework.frankdoc.testtarget.doclet.interfaces.";

	FrankClassRepository classRepository;

	@BeforeEach
	public void setUp() {
		classRepository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
	}

	@Test
	public void whenClassInheritsInterfaceThroughChildInterfacesThenNotReturnedByMethodInterfaces() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "DiamondImplementationOfCommonParent");
		List<String> actualInterfaceSimpleNames = getSimpleNamesOfImplementedInterfaces(clazz);
		String[] expectedInterfaceSimpleNames = new String[] {"FirstChildOfCommonParent", "SecondChildOfCommonParent"};
		assertArrayEquals(expectedInterfaceSimpleNames, actualInterfaceSimpleNames.toArray(new String[] {}));
	}

	private List<String> getSimpleNamesOfImplementedInterfaces(FrankClass clazz) {
		return Arrays.asList(clazz.getInterfaces()).stream()
				.map(FrankClass::getSimpleName)
				.collect(Collectors.toList());
	}

	@Test
	public void whenClassAlreadyImplementsInterfaceViaChildInterfaceThenParentInterfaceStillReturnedByMethodInterfaces() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "ImplementationWithMeaninglessImportOfParentInterface");
		List<String> actualInterfaceSimpleNames = getSimpleNamesOfImplementedInterfaces(clazz);
		String[] expectedInterfaceSimpleNames = new String[] {"ParentOfTwoChildren", "FirstChildOfCommonParent"};
		assertArrayEquals(expectedInterfaceSimpleNames, actualInterfaceSimpleNames.toArray(new String[] {}));
	}

	@Test
	public void whenClassAlreadyImplementsInterfaceViaChildInterfaceThenParentInterfaceStillReturnedByMethodInterfaces2() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "ImplementationWithMeaninglessImportOfParentInterface2");
		List<String> actualInterfaceSimpleNames = getSimpleNamesOfImplementedInterfaces(clazz);
		String[] expectedInterfaceSimpleNames = new String[] {"FirstChildOfCommonParent", "ParentOfTwoChildren"};
		assertArrayEquals(expectedInterfaceSimpleNames, actualInterfaceSimpleNames.toArray(new String[] {}));
	}

	@Test
	public void browseTransitiveImplementedInterfaces() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "ImplementationWithMeaninglessImportOfParentInterface");
		String[] actual = browseTransitiveInterfaceImplementations(clazz);
		String[] expected = new String[] {"ParentOfTwoChildren", "FirstChildOfCommonParent", "GrandParent1", "GrandParent2"};
		assertArrayEquals(expected, actual);
	}

	@Test
	public void browseTransitiveImplementedInterfaces2() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "ImplementationWithMeaninglessImportOfParentInterface2");
		String[] actual = browseTransitiveInterfaceImplementations(clazz);
		String[] expected = new String[] {"FirstChildOfCommonParent", "ParentOfTwoChildren", "GrandParent1", "GrandParent2"};
		assertArrayEquals(expected, actual);
	}

	private String[] browseTransitiveInterfaceImplementations(FrankClass clazz) throws FrankDocException {
		final List<String> browsedClassSimpleNames = new ArrayList<>();
		TransitiveImplementedInterfaceBrowser<String> browser = new TransitiveImplementedInterfaceBrowser<String>(clazz);
		browser.search(c -> handle(c, browsedClassSimpleNames));
		return browsedClassSimpleNames.toArray(new String[] {});
	}

	private String handle(FrankClass clazz, final List<String> visited) {
		visited.add(clazz.getSimpleName());
		return null;
	}

	@Test
	public void searchingStopsWhenTargetIsFound() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "ImplementationWithMeaninglessImportOfParentInterface");
		SearchResult actual = browseTransitiveInterfaceImplementations(clazz, "FirstChildOfCommonParent");
		String[] expectedVisited = new String[] {"ParentOfTwoChildren", "FirstChildOfCommonParent"};
		assertArrayEquals(expectedVisited, actual.visitedClassSimpleNames);
		assertEquals("found", actual.searchResult);
	}

	private static class SearchResult {
		final String[] visitedClassSimpleNames;
		final String searchResult;

		SearchResult(String[] visitedClassSimpleNames, String searchResult) {
			this.visitedClassSimpleNames = visitedClassSimpleNames;
			this.searchResult = searchResult;
		}
	}

	private SearchResult browseTransitiveInterfaceImplementations(FrankClass clazz, final String foundWhenSimpleNameIs) throws FrankDocException {
		final List<String> browsedClassSimpleNames = new ArrayList<>();
		TransitiveImplementedInterfaceBrowser<String> browser = new TransitiveImplementedInterfaceBrowser<String>(clazz);
		String result = browser.search(c -> handleForSearchOf(c, foundWhenSimpleNameIs, browsedClassSimpleNames));
		return new SearchResult(browsedClassSimpleNames.toArray(new String[] {}), result);
	}

	private String handleForSearchOf(FrankClass clazz, String foundWhenSimpleNameIs, final List<String> visited) {
		visited.add(clazz.getSimpleName());
		if(clazz.getSimpleName().equals(foundWhenSimpleNameIs)) {
			return "found";
		} else {
			return null;
		}
	}

	@Test
	public void whenMatchingInterfaceMethodHasAnnotationThenAnnotationFound() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "DiamondImplementationOfCommonParent");
		List<FrankMethod> actualDeclaredMethods = getNonJacocoDeclaredMethods(clazz);
		assertEquals(1, actualDeclaredMethods.size());
		FrankMethod method = actualDeclaredMethods.get(0);
		assertEquals("annotatedMethod", method.getName());
		assertNull(method.getAnnotation(TestUtil.DEPRECATED));
		FrankAnnotation annotation = method.getAnnotationIncludingInherited(TestUtil.DEPRECATED);
		assertEquals(TestUtil.DEPRECATED, annotation.getName());
	}

	private List<FrankMethod> getNonJacocoDeclaredMethods(FrankClass clazz) {
		return Arrays.asList(clazz.getDeclaredMethods()).stream()
				.filter(c -> ! c.getName().contains("jacoco"))
				.collect(Collectors.toList());
	}

	@Test
	public void whenSuperclassHasMatchingInterfaceMethodWithAnnotationThenAnnotationFound() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "GrandChild");
		List<FrankMethod> actualDeclaredMethods = getNonJacocoDeclaredMethods(clazz);
		assertEquals(1, actualDeclaredMethods.size());
		FrankMethod method = actualDeclaredMethods.get(0);
		assertEquals("annotatedMethod", method.getName());
		assertNull(method.getAnnotation(TestUtil.DEPRECATED));
		FrankAnnotation annotation = method.getAnnotationIncludingInherited(TestUtil.DEPRECATED);
		assertEquals(TestUtil.DEPRECATED, annotation.getName());
	}

	@Test
	public void whenAbstractSuperclassHasMatchingInterfaceMethodWithAnnotationThenAnnotationFound() throws FrankDocException {
		FrankClass clazz = classRepository.findClass(PACKAGE + "ChildOfAbstractImplementation");
		List<FrankMethod> actualDeclaredMethods = getNonJacocoDeclaredMethods(clazz);
		String actualDeclaredMethodsString = actualDeclaredMethods.stream()
				.map(FrankMethod::getName)
				.collect(Collectors.joining(", "));
		assertEquals(1, actualDeclaredMethods.size(), String.format("Have methods [%s]",  actualDeclaredMethodsString));
		FrankMethod method = actualDeclaredMethods.get(0);
		assertEquals("annotatedMethod", method.getName());
		assertNull(method.getAnnotation(TestUtil.DEPRECATED));
		FrankAnnotation annotation = method.getAnnotationIncludingInherited(TestUtil.DEPRECATED);
		assertEquals(TestUtil.DEPRECATED, annotation.getName());
	}
}
