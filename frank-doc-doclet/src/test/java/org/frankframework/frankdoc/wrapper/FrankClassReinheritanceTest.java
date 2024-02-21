package org.frankframework.frankdoc.wrapper;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class FrankClassReinheritanceTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.wrapper.reinherit.";
	private static final String SUBJECT = PACKAGE + "Subject";

	@Test
	public void testReInheritedMethods() throws Exception {
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		FrankClass subject = repository.findClass(SUBJECT);
		List<MultiplyInheritedMethodPlaceholder> actualPlaceholders = subject.getMultiplyInheritedMethodPlaceholders();
		List<String> actual = actualPlaceholders.stream().map(FrankMethod::getName).sorted().collect(Collectors.toList());
		assertArrayEquals(new String[] { "reInherited1", "reInherited2", "reInherited3", "reInherited4" }, actual.toArray(new String[] {}));
	}
}
