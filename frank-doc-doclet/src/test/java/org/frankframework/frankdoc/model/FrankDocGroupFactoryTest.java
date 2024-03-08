package org.frankframework.frankdoc.model;

import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

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
		FrankDocGroupFactory instance = new FrankDocGroupFactory(repository);
		FrankDocGroup first = instance.getGroup(repository.findClass(PACKAGE + FIRST_LISTENER));
		assertEquals("Listener", first.getName());
		FrankDocGroup second = instance.getGroup(repository.findClass(PACKAGE + SECOND_LISTENER));
		assertSame(first, second);
		// Test that group Other is always created
		assertArrayEquals(new String[] {"Listener", "Other"}, getGroupNames(instance));
	}

	private static String[] getGroupNames(FrankDocGroupFactory instance) {
		return instance.getAllGroups().stream()
			.map(FrankDocGroup::getName)
			.toList()
			.toArray(new String[] {});
	}

	@Test
	public void whenClassWithoutGroupThenGroupOther() throws Exception {
		FrankDocGroupFactory instance = new FrankDocGroupFactory(repository);
		FrankDocGroup listener = instance.getGroup(repository.findClass(PACKAGE + FIRST_LISTENER));
		assertEquals("Listener", listener.getName());
		FrankDocGroup firstOther = instance.getGroup(repository.findClass(PACKAGE + OTHER));
		assertEquals("Other", firstOther.getName());;
		FrankDocGroup secondOther = instance.getGroup(repository.findClass(PACKAGE + SECOND_OTHER));
		assertSame(firstOther, secondOther);
		assertArrayEquals(new String[] {"Listener", "Other"}, getGroupNames(instance));
	}

	@Test
	public void groupsAreOrderedByEnum() throws Exception {
		FrankDocGroupFactory instance = new FrankDocGroupFactory(repository);
		instance.getGroup(repository.findClass(PACKAGE + FIRST_LISTENER));
		instance.getGroup(repository.findClass(PACKAGE + ASender));
		instance.getGroup(repository.findClass(PACKAGE + APipe));
		instance.getGroup(repository.findClass(PACKAGE + OTHER));
		assertArrayEquals(new String[] {"Pipe", "Sender", "Listener", "Other"}, getGroupNames(instance));
	}
}
