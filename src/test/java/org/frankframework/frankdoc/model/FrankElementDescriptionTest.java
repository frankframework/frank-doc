package org.frankframework.frankdoc.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class FrankElementDescriptionTest {
	@Test
	public void whenDescriptionMissesDotSpaceThenDescriptionHeaderIsSame() {
		String description = "This is a sentence.";
		assertEquals("This is a sentence.", FrankElement.calculateDescriptionHeader(description));
	}

	@Test
	public void whenDescriptionMissesDotThenNoExtraDotAdded() {
		String description = "This is a sentence";
		assertEquals("This is a sentence", FrankElement.calculateDescriptionHeader(description));		
	}

	@Test
	public void whenDescriptionHasDotSpaceThenDescriptionHeaderFirstSentenceWithDot() {
		String description = "This is a sentence. This is the second sentence";
		assertEquals("This is a sentence.", FrankElement.calculateDescriptionHeader(description));		
	}

	@Test
	public void whenDescriptionHasDotNewlineThenDescriptionHeaderFirstSentenceWithDot() {
		String description = "This is a sentence.\nThis is the second sentence";
		assertEquals("This is a sentence.", FrankElement.calculateDescriptionHeader(description));		
	}

	@Test
	public void whenDescriptionHasSimpleHtmlTagThenFound() {
		List<String> actual = FrankElement.getHtmlTags("This <element> is an element");
		assertArrayEquals(new String[] {"element"}, actual.toArray(new String[] {}));
	}

	@Test
	public void whenHtmlTagWithAttributesThenTagFound() {
		List<String> actual = FrankElement.getHtmlTags("Link <a href=\"http://myDomain\">Title of link</a> is a link");
		assertArrayEquals(new String[] {"a"}, actual.toArray(new String[] {}));
	}

	@Test
	public void whenNoHtmlTagsThenEmptyList() {
		assertTrue(FrankElement.getHtmlTags("No tags").isEmpty());
	}

	@Test
	public void whenMultipleHtmlTagsThenAllFound() {
		List<String> actual = FrankElement.getHtmlTags("With <code>MyCode</code> and a <a href=\"http://myDomain\">Link</a>.");
		assertArrayEquals(new String[] {"code", "a"}, actual.toArray(new String[] {}));
	}
}
