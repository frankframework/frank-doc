package org.frankframework.frankdoc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttributeValuesFactoryTest {
	private AttributeEnumFactory instance;

	@BeforeEach
	public void setUp() {
		instance = new AttributeEnumFactory();
	}

	@Test
	public void whenSameClassRequestedMultipleTimesThenOnlyOnceAdded() {
		instance.findOrCreateAttributeEnum("foo.Bar", "Bar", new ArrayList<>());
		instance.findOrCreateAttributeEnum("foo.Bar", "Bar", new ArrayList<>());
		assertEquals(1, instance.size());
		AttributeEnum item = instance.findAttributeEnum("foo.Bar");
		assertEquals("BarList", item.getUniqueName("List"));
	}

	@Test
	public void whenDifferentWithSameSimpleNameAddedThenMultipleCreated() {
		instance.findOrCreateAttributeEnum("foo.Bar", "Bar", new ArrayList<>());
		instance.findOrCreateAttributeEnum("baz.Bar", "Bar", new ArrayList<>());
		assertEquals(2, instance.size());
		AttributeEnum item = instance.findAttributeEnum("foo.Bar");
		assertEquals("BarList", item.getUniqueName("List"));
		item = instance.findAttributeEnum("baz.Bar");
		assertEquals("BarList_2", item.getUniqueName("List"));
	}
}
