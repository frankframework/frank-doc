package org.frankframework.frankdoc.model;

import org.frankframework.frankdoc.Utils;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class AttributeTypeTest {
	@Test
	public void everyPrimitiveTypeOrBoxedHasAnAttributeType() {
		Utils.ALLOWED_SETTER_TYPES.forEach(t -> AttributeType.fromJavaType(t));
	}

	@Test
	public void everyAttributeTypeHasJavaType() {
		final Set<AttributeType> usedAttributeTypes = new HashSet<>();
		Utils.ALLOWED_SETTER_TYPES.forEach(t -> usedAttributeTypes.add(AttributeType.fromJavaType(t)));
		Set<AttributeType> unusedAttributeTypes = new HashSet<>(Arrays.asList(AttributeType.values()));
		unusedAttributeTypes.removeAll(usedAttributeTypes);
		assertTrue(unusedAttributeTypes.isEmpty());
	}
}
