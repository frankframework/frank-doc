package org.frankframework.frankdoc;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsFlattenJavaDocLinksTest {
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{"{@link Receiver Receivers}", "Receivers"},
			{"Some {@link Receiver Receivers} here", "Some Receivers here"},
			{" {@link} ", "  "},
			{"Some {@link nl.nn.Receiver}", "Some nl.nn.Receiver"},
			{"{@link Receiver} and {@link Adapter}", "Receiver and Adapter"},
			{"Some {@link Receiver} and {@link Adapter} here", "Some Receiver and Adapter here"},
			// There is a tab between "Receiver" and "Receivers".
			{"Some {@link Receiver	Receivers} here", "Some Receivers here"}
		});
	}
	public String input;
	public String expected;

	@MethodSource("data")
	@ParameterizedTest(name = "{0}")
	public void test(String input, String expected) throws Exception {
		initUtilsFlattenJavaDocLinksTest(input, expected);
		String actual = Utils.flattenJavaDocLinksToLastWords(input);
		assertEquals(expected, actual);
	}

	public void initUtilsFlattenJavaDocLinksTest(String input, String expected) {
		this.input = input;
		this.expected = expected;
	}
}
