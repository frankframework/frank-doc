/*
Copyright 2021 WeAreFrank!

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.frankframework.frankdoc;

import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.frankframework.frankdoc.Utils.isConfigChildSetter;
import static org.frankframework.frankdoc.Utils.replacePattern;
import static org.frankframework.frankdoc.Utils.*;
import static org.frankframework.frankdoc.wrapper.TestUtil.assertJsonEqual;
import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {
	private static final String SIMPLE = "org.frankframework.frankdoc.testtarget.simple";
	private FrankClassRepository repository;

	@BeforeEach
	public void setUp() {
		repository = TestUtil.getFrankClassRepositoryDoclet(SIMPLE);
	}

	@Test
	public void testGetSpringBeans() throws FrankDocException {
		List<FrankClass> actual = repository.findClass(SIMPLE + ".IListener").getInterfaceImplementations();
		Collections.sort(actual, Comparator.comparing(FrankClass::getName));
		assertEquals(4, actual.size());
		Iterator<FrankClass> it = actual.iterator();
		FrankClass first = it.next();
		assertEquals(SIMPLE + ".ListenerChild", first.getName());
		FrankClass second = it.next();
		assertEquals(SIMPLE + ".ListenerGrandChild", second.getName());
		FrankClass third = it.next();
		assertEquals(SIMPLE + ".ListenerParent", third.getName());
	}

	@Test
	public void whenMethodIsConfigChildSetterThenRecognized() throws FrankDocException {
		assertTrue(isConfigChildSetter(getTestMethod("setListener")));
	}

	private FrankMethod getTestMethod(String name) throws FrankDocException {
		FrankClass listenerChildClass = repository.findClass(SIMPLE + ".ListenerChild");
		for(FrankMethod m: listenerChildClass.getDeclaredAndInheritedMethods()) {
			if(m.getName().equals(name)) {
				return m;
			}
		}
		throw new RuntimeException("No method in ListenerChild for method name: " + name);
	}

	@Test
	public void whenAttributeSetterThenNotConfigChildSetter() throws FrankDocException {
		assertFalse(isConfigChildSetter(getTestMethod("setChildAttribute")));
	}

	@Test
	public void whenMethodHasTwoArgsThenNotConfigChildSetter() throws FrankDocException {
		assertFalse(isConfigChildSetter(getTestMethod("invalidConfigChildSetterTwoArgs")));
	}

	@Test
	public void whenMethodReturnsPrimitiveThenNotConfigChildSetter() throws FrankDocException {
		assertFalse(isConfigChildSetter(getTestMethod("invalidConfigChildSetterReturnsInt")));
	}

	@Test
	public void whenMethodReturnsStringThenNotConfigChildSetter()  throws FrankDocException {
		assertFalse(isConfigChildSetter(getTestMethod("invalidConfigChildSetterReturnsString")));
	}

	@Test
	public void whenMethodStartsWithAddAndTakesStringThenTextConfigChildSetter() throws FrankDocException {
		assertTrue(isConfigChildSetter(getTestMethod("addTextConfigChild")));
	}

	@Test
	public void whenMethodStartsWithSetAndTakesStringThenNotConfigChildSetter() throws FrankDocException {
		assertFalse(isConfigChildSetter(getTestMethod("setNotTextConfigChildButAttribute")));
	}

	@Test
	public void whenStringHasUnfinishedJavaDocLinkThenWarn() {
		try (TestAppender appender = TestAppender.newBuilder().build()) {
			// No closing "}"
			Utils.flattenJavaDocLinksToLastWords("{@link Receiver");

			appender.assertLogged("Unfinished JavaDoc {@ ...} pattern text [{@link Receiver] at index [0]");
		}
	}

	@Test
	public void nullEqualsNull() {
		assertTrue(Utils.equalsNullable(null, null));
	}

	@Test
	public void nullDoesNotEqualNonNull() {
		assertFalse(Utils.equalsNullable(null, "something"));
	}

	@Test
	public void nonNullDoesNotEqualNull() {
		assertFalse(Utils.equalsNullable("something", null));
	}

	@Test
	public void nonEqualObjectsAreNotEqualNullable() {
		assertFalse(Utils.equalsNullable("this", "other"));
	}

	@Test
	public void equalObjectsAreEqualNullable() {
		assertTrue(Utils.equalsNullable("this", "thi" + "s"));
	}

	@Test
	public void whenJsonObjectsHaveDifferentKeyOrderThenTheyShouldBeEqual() {
		TestUtil.assertJsonEqual("Comparing objects with differently ordered elements", "{\"a\":1,\"b\":2}", "{\"b\":2,\"a\":1}");
	}

	@Test
	public void whenJsonObjectsHaveDifferentFormattingThenTheyShouldBeEqual() {
		TestUtil.assertJsonEqual("Comparing json objects", "{\"a\":1,\"b\":2 }", "{ \"a\" :1,\"b\":2}");
	}

	@Test
	public void whenJsonObjectIsSimpleAndUnsortedThenItShouldBeOrderedCorrectly() {
		String input = "{\"z\":\"last\",\"a\":\"first\",\"m\":\"middle\"}";
		String expected = "{\"a\":\"first\",\"m\":\"middle\",\"z\":\"last\"}";

		String actual = Utils.jsonOrder(input);
		assertEquals(expected, actual);
	}

	@Test
	public void whenJsonObjectIsAlreadySortedThenItShouldRemainUnchanged() {
		String input = "{\"a\":1,\"b\":2,\"c\":3}";
		String expected = "{\"a\":1,\"b\":2,\"c\":3}";

		String actual = Utils.jsonOrder(input);
		assertEquals(expected, actual);
	}

	@Test
	public void whenJsonObjectHasReversedKeysThenItShouldBeOrderedCorrectly() {
		String input = "{\"c\":3,\"b\":2,\"a\":1}";
		String expected = "{\"a\":1,\"b\":2,\"c\":3}";

		String actual = Utils.jsonOrder(input);
		assertEquals(expected, actual);
	}

	@Test
	public void whenJsonObjectHasMixedValueTypesThenItShouldBeOrderedCorrectly() {
		String input = "{\"z\":true,\"a\":123,\"m\":\"text\"}";
		String expected = "{\"a\":123,\"m\":\"text\",\"z\":true}";

		String actual = Utils.jsonOrder(input);
		assertEquals(expected, actual);
	}

	@Test
	public void whenJsonObjectIsEmptyThenItShouldRemainEmpty() {
		String input = "{}";
		String expected = "{}";

		String actual = Utils.jsonOrder(input);
		assertEquals(expected, actual);
	}

	@Test
	public void whenJsonObjectsAreOrderedDifferentlyThenTheyShouldBeEqual() {
		String expected = "{\"a\":1,\"b\":2,\"c\":3}";
		String actual = "{\"c\":3,\"b\":2,\"a\":1}";

		// Should not throw
		TestUtil.assertJsonEqual("JSON objects should be equal", expected, actual);
	}

	@Test
	public void whenJsonObjectsDifferInValuesThenTheyShouldNotBeEqual() {
		String expected = "{\"a\":1,\"b\":2}";
		String actual = "{\"a\":1,\"b\":3}";

		// Should throw AssertionError
		assertThrows(AssertionError.class, () ->
			TestUtil.assertJsonEqual("JSON mismatch", expected, actual)
		);
	}

	@Test
	public void whenJsonObjectsAreIdenticalThenTheyShouldBeEqual() {
		String expected = "{\"x\":\"y\"}";
		String actual = "{\"x\":\"y\"}";

		TestUtil.assertJsonEqual("Identical JSON should match", expected, actual);
	}

	@Test
	public void whenJsonObjectsHaveWhitespaceDifferencesThenTheyShouldBeEqual() {
		String expected = "{ \"a\": 1, \"b\": 2 }";
		String actual = "{\"b\":2,\"a\":1}";

		TestUtil.assertJsonEqual("Whitespace and order differences should be ignored", expected, actual);
	}

	@Test
	public void whenDescriptionHasSimpleHtmlTagThenFound() {
		List<String> actual = Utils.getHtmlTags("This <element> is an element");
		assertArrayEquals(new String[] {"element"}, actual.toArray(new String[] {}));
	}

	@Test
	public void whenHtmlTagWithAttributesThenTagFound() {
		List<String> actual = Utils.getHtmlTags("Link <a href=\"http://myDomain\">Title of link</a> is a link");
		assertArrayEquals(new String[] {"a"}, actual.toArray(new String[] {}));
	}

	@Test
	public void whenNoHtmlTagsThenEmptyList() {
		assertTrue(Utils.getHtmlTags("No tags").isEmpty());
	}

	@Test
	public void whenMultipleHtmlTagsThenAllFound() {
		List<String> actual = Utils.getHtmlTags("With <code>MyCode</code> and a <a href=\"http://myDomain\">Link</a>.");
		assertArrayEquals(new String[] {"code", "a"}, actual.toArray(new String[] {}));
	}

	@Test
	@Timeout(value=25, unit=TimeUnit.MILLISECONDS)
	public void testReplacePattern() throws FrankDocException {
		String result1 = replacePattern("start{@code aaaa{bbbb}cccc}end{@code dddd}end", JAVADOC_CODE_START_DELIMITER, s -> "<tag>" + s + "</tag>");
		String result2 = replacePattern("start{@code {@value value} {@link link} }end", JAVADOC_CODE_START_DELIMITER, s -> "<tag>" + s + "</tag>");
		String result3 = replacePattern("start{@code {}{{{{{{{}{{}}{{{}}}}}}}}} }end", JAVADOC_CODE_START_DELIMITER, s -> "<tag>" + s + "</tag>");
		String result4 = replacePattern("start{@code\t\n code \n block\t\n }end", JAVADOC_CODE_START_DELIMITER, s -> "<tag>" + s + "</tag>");

		assertEquals("start<tag>aaaa{bbbb}cccc</tag>end<tag>dddd</tag>end", result1);
		assertEquals("start<tag>{@value value} {@link link}</tag>end", result2);
		assertEquals("start<tag>{}{{{{{{{}{{}}{{{}}}}}}}}}</tag>end", result3);
		assertEquals("start<tag>code \n block</tag>end", result4);
	}

}
