package org.frankframework.frankdoc.wrapper;

import static org.junit.Assert.assertArrayEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class FrankClassDocletReinheritanceTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.wrapper.reinherit.";
	private static final String SUBJECT = PACKAGE + "Subject";

	@Test
	public void testReInheritedMethods() throws Exception {
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		FrankClass subject = repository.findClass(SUBJECT);
		List<MultiplyInheritedMethodPlaceholder> actualPlaceholders = ((FrankClassDoclet) subject).getMultiplyInheritedMethodPlaceholders();
		List<String> actual = actualPlaceholders.stream().map(FrankMethod::getName).sorted().collect(Collectors.toList());
		assertArrayEquals(new String[] { "reInherited1", "reInherited2", "reInherited3", "reInherited4" }, actual.toArray(new String[] {}));
	}
}
