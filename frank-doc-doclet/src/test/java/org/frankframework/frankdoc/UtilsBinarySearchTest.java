package org.frankframework.frankdoc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;

/**
 * Unit tests of methods {@link Utils#addToSortedListNonUnique(List, Object)} and {@link Utils#addToSortedListUnique(List, Object)}.
 * @author martijn
 *
 */
@RunWith(Parameterized.class)
public class UtilsBinarySearchTest {
	@Parameters
	public static Collection<Object[]> data() {
		return asList(new Object[][] {
			{asList(), "A", asList("A"), asList("A")},
			{asList("A"), "A", asList("A", "A"), asList("A")},
			{asList("A", "C"), "B", asList("A", "B", "C"), asList("A", "B", "C")},
			{asList("A", "B", "C"), "B", asList("A", "B", "B", "C"), asList("A", "B", "C")}
		});
	}

	@Parameter(value = 0)
	public List<String> start;

	@Parameter(value = 1)
	public String added;

	@Parameter(value = 2)
	public List<String> afterAddNonUnique;

	@Parameter(value = 3)
	public List<String> afterAddUnique;

	@Test
	public void testNonUnique() {
		List<String> input = new ArrayList<>(start);
		Utils.addToSortedListNonUnique(input, added);
		assertArrayEquals(afterAddNonUnique.toArray(), input.toArray());
	}

	@Test
	public void testUnique() {
		List<String> input = new ArrayList<>(start);
		Utils.addToSortedListUnique(input, added);
		assertArrayEquals(afterAddUnique.toArray(), input.toArray());
	}
}
