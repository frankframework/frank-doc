package org.frankframework.frankdoc.model;

import static org.junit.Assert.assertArrayEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.Test;

public class ReInheritedMethodsTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.reinherit.";
	private static final String SUBJECT = PACKAGE + "Subject";

	@Test
	public void testReInheritedMethods() throws Exception {
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		FrankClass subject = repository.findClass(SUBJECT);
		List<String> actual = ReInheritedMethods.findFor(subject).stream()
				.map(FrankMethod::getName)
				.sorted()
				.collect(Collectors.toList());
		assertArrayEquals(new String[] {"reInherited1", "reInherited2", "reInherited3", "reInherited4"}, actual.toArray(new String[] {}));
	}
}
