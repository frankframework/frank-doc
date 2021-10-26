package org.frankframework.frankdoc.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class SpecificParameterTest {
	@Test
	public void whenParamTagHasNoSpaceThenOnlyName() {
		ParsedJavaDocTag p = ParsedJavaDocTag.getInstance("myName");
		assertNull(p.getDescription());
		assertEquals("myName", p.getName());
	}

	@Test
	public void whenParamTagHasSpacesAfterNameThenStillDescriptionNull() {
		ParsedJavaDocTag p = ParsedJavaDocTag.getInstance("myName ");
		assertNull(p.getDescription());
		assertEquals("myName", p.getName());		
	}

	@Test
	public void whenParamTagHasMultipleWordsThenNameAndDescription() {
		ParsedJavaDocTag p = ParsedJavaDocTag.getInstance("myName        Description ");
		assertEquals("myName", p.getName());
		assertEquals("Description", p.getDescription());
	}
}
