package org.frankframework.frankdoc.wrapper;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.sun.javadoc.ClassDoc;

/**
 * Tests asking a class for the interfaces it implements. if you ask a class for the interfaces
 * it implements, then the filters configured in
 * {@link FrankClassRepository#getDocletInstance(com.sun.javadoc.ClassDoc[], java.util.Set, java.util.Set, java.util.Set)}
 * are not applied.
 * 
 * Test {@link FilteringInterfaceImplementationsTest}
 * tests asking an interface for the classes it implements. Working in that direction, the filtering
 * configured in {@link FrankClassRepository#getDocletInstance(com.sun.javadoc.ClassDoc[], java.util.Set, java.util.Set, java.util.Set)}
 * is applied.
 *
 * @author martijn
 *
 */
public class NoFilteringInterfaceImplementationsTest extends FilteringTestBase {
	@Test
	public void whenClassImplementsExcludedInterfaceThenClassStillSeesItAsAncestor() throws Exception {
		ClassDoc[] classDocs = TestUtil.getClassDocs(BOTH_PACKAGES);
		FrankClassRepository repository = FrankClassRepository.getDocletInstance(classDocs, new HashSet<>(asList(SECOND_PACKAGE)), new HashSet<>(asList(NO_EXCLUDES)), new HashSet<>());
		FrankClass clazz = repository.findClass(SECOND_PACKAGE + SECOND_IMPL);
		FrankClass[] actualInterfacesAry = clazz.getInterfaces();
		List<String> actual = asList(actualInterfacesAry).stream().map(FrankClass::getName).collect(Collectors.toList());
		assertArrayEquals(new String[] {FIRST_PACKAGE + "MyInterface"}, actual.toArray(new String[] {}));
	}
}
