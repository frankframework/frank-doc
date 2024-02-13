package org.frankframework.frankdoc;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Unit tests of methods {@link Utils#addToSortedListNonUnique(List, Object)} and {@link Utils#addToSortedListUnique(List, Object)}.
 * @author martijn
 *
 */
public class UtilsBinarySearchTest {
	public static Collection<Object[]> data() {
		return asList(new Object[][] {
			{asList(), "A", asList("A"), asList("A")},
			{asList("A"), "A", asList("A", "A"), asList("A")},
			{asList("A", "C"), "B", asList("A", "B", "C"), asList("A", "B", "C")},
			{asList("A", "B", "C"), "B", asList("A", "B", "B", "C"), asList("A", "B", "C")}
		});
	}
	public List<String> start;
	public String added;
	public List<String> afterAddNonUnique;
	public List<String> afterAddUnique;

	@MethodSource("data")
	@ParameterizedTest
	public void testNonUnique(List<String> start, String added, List<String> afterAddNonUnique, List<String> afterAddUnique) {
		initUtilsBinarySearchTest(start, added, afterAddNonUnique, afterAddUnique);
		List<String> input = new ArrayList<>(start);
		Utils.addToSortedListNonUnique(input, added);
		assertArrayEquals(afterAddNonUnique.toArray(), input.toArray());
	}

	@MethodSource("data")
	@ParameterizedTest
	public void testUnique(List<String> start, String added, List<String> afterAddNonUnique, List<String> afterAddUnique) {
		initUtilsBinarySearchTest(start, added, afterAddNonUnique, afterAddUnique);
		List<String> input = new ArrayList<>(start);
		Utils.addToSortedListUnique(input, added);
		assertArrayEquals(afterAddUnique.toArray(), input.toArray());
	}

	public void initUtilsBinarySearchTest(List<String> start, String added, List<String> afterAddNonUnique, List<String> afterAddUnique) {
		this.start = start;
		this.added = added;
		this.afterAddNonUnique = afterAddNonUnique;
		this.afterAddUnique = afterAddUnique;
	}
}
