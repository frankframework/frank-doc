package org.frankframework.frankdoc.model;

import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.stream.Collectors;

public class FrankDocGroupFactoryTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.group.factory.";
	private static final String FIRST_LISTENER = "FirstListener";
	private static final String SECOND_LISTENER = "SecondListener";
	private static final String APipe = "APipe";
	private static final String ASender = "ASender";
	private static final String OTHER = "Other";
	private static final String SECOND_OTHER = "SecondOther";

	private FrankClassRepository repository;

	@BeforeEach
	public void setUp() {
		repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE, "org.frankframework.doc.");
	}

	@Test
	public void whenClassesHaveSameGroupThenOnlyOneGroupCreated() throws Exception {
		FrankDocGroupFactory instance = new FrankDocGroupFactory();
		FrankDocGroup first = instance.getGroup(repository.findClass(PACKAGE + FIRST_LISTENER));
		Assertions.assertEquals("Listeners", first.getName());
		FrankDocGroup second = instance.getGroup(repository.findClass(PACKAGE + SECOND_LISTENER));
		Assertions.assertSame(first, second);
		// Also test that group Others is omitted when every class has an explicit group
		Assertions.assertArrayEquals(new String[] {"Listeners"}, getGroupNames(instance));
	}

	private static String[] getGroupNames(FrankDocGroupFactory instance) {
		return instance.getAllGroups().stream()
			.map(FrankDocGroup::getName)
			.toList()
			.toArray(new String[] {});
	}

	@Test
	public void whenClassWithoutGroupThenGroupOther() throws Exception {
		FrankDocGroupFactory instance = new FrankDocGroupFactory();
		FrankDocGroup listener = instance.getGroup(repository.findClass(PACKAGE + FIRST_LISTENER));
		Assertions.assertEquals("Listeners", listener.getName());
		FrankDocGroup firstOther = instance.getGroup(repository.findClass(PACKAGE + OTHER));
		Assertions.assertEquals("Other", firstOther.getName());;
		FrankDocGroup secondOther = instance.getGroup(repository.findClass(PACKAGE + SECOND_OTHER));
		Assertions.assertSame(firstOther, secondOther);
		Assertions.assertArrayEquals(new String[] {"Listeners", "Other"}, getGroupNames(instance));
	}

	@Test
	public void groupsAreOrderedByEnum() throws Exception {
		FrankDocGroupFactory instance = new FrankDocGroupFactory();
		instance.getGroup(repository.findClass(PACKAGE + FIRST_LISTENER));
		instance.getGroup(repository.findClass(PACKAGE + ASender));
		instance.getGroup(repository.findClass(PACKAGE + APipe));
		instance.getGroup(repository.findClass(PACKAGE + OTHER));
		Assertions.assertArrayEquals(new String[] {"Pipes", "Senders", "Listeners", "Other"}, getGroupNames(instance));
	}
}
