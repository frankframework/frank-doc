package org.frankframework.frankdoc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class UtilsFlattenJavaDocLinksTest {
	@Parameters(name = "{0}")
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

	@Parameter(0)
	public String input;

	@Parameter(1)
	public String expected;

	@Test
	public void test() throws Exception {
		String actual = Utils.flattenJavaDocLinksToLastWords(input);
		assertEquals(expected, actual);
	}
}
