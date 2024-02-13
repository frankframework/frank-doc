package org.frankframework.frankdoc.model;

import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParsedJavaDocTagTest {
	@Test
	public void whenParamTagHasNoSpaceThenOnlyName() throws Exception {
		ParsedJavaDocTag p = ParsedJavaDocTag.getInstance("myName");
		assertNull(p.getDescription());
		assertEquals("myName", p.getName());
	}

	@Test
	public void whenParamTagHasSpacesAfterNameThenStillDescriptionNull() throws Exception {
		ParsedJavaDocTag p = ParsedJavaDocTag.getInstance("myName ");
		assertNull(p.getDescription());
		assertEquals("myName", p.getName());
	}

	@Test
	public void whenParamTagHasMultipleWordsThenNameAndDescription() throws Exception {
		ParsedJavaDocTag p = ParsedJavaDocTag.getInstance("myName        Description ");
		assertEquals("myName", p.getName());
		assertEquals("Description", p.getDescription());
	}

	@Test
	public void whenAnnotationValueStartsWithQuoteThenNameIsStringUntilEndQuote() throws Exception {
		ParsedJavaDocTag p = ParsedJavaDocTag.getInstance("\"My quoted name\" Description");
		assertEquals("My quoted name", p.getName());
		assertEquals("Description", p.getDescription());
	}

	@Test
	public void whenQuotedForwardNameIsEmptyStringAndDescriptionRightAfterLastQuoteThenNoError() throws Exception {
		ParsedJavaDocTag p = ParsedJavaDocTag.getInstance("\"\"Description");
		assertEquals("", p.getName());
		assertEquals("Description", p.getDescription());
	}

	@Test
	public void whenJavaDocHasNoParametersThenError() throws Exception {
		assertThrows(FrankDocException.class, () -> {
			ParsedJavaDocTag.getInstance("");
		});
	}
}
