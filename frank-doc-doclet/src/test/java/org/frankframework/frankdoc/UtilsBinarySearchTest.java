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
 */
public class UtilsBinarySearchTest {
	public static Collection<Object[]> data() {
		return asList(new Object[][]{
			{List.of(), "A", List.of("A"), List.of("A")},
			{List.of("A"), "A", asList("A", "A"), List.of("A")},
			{asList("A", "C"), "B", asList("A", "B", "C"), asList("A", "B", "C")},
			{asList("A", "B", "C"), "B", asList("A", "B", "B", "C"), asList("A", "B", "C")}
		});
	}

	public List<String> start;

	@MethodSource("data")
	@ParameterizedTest
	void testNonUnique(List<String> start, String added, List<String> afterAddNonUnique, List<String> afterAddUnique) {
		List<String> input = new ArrayList<>(start);
		Utils.addToSortedListNonUnique(input, added);
		assertArrayEquals(afterAddNonUnique.toArray(), input.toArray());
	}

	@MethodSource("data")
	@ParameterizedTest
	void testUnique(List<String> start, String added, List<String> afterAddNonUnique, List<String> afterAddUnique) {
		List<String> input = new ArrayList<>(start);
		Utils.addToSortedListUnique(input, added);
		assertArrayEquals(afterAddUnique.toArray(), input.toArray());
	}

}
