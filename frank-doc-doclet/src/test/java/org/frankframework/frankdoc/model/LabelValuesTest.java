package org.frankframework.frankdoc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LabelValuesTest {
	private LabelValues instance;

	@BeforeEach
	public void setUp() {
		instance = new LabelValues();
	}

	@Test
	public void valuesAreCorrectlyStoredWithNames() {
		instance.addValue("FIRST", "valueOfFirst");
		instance.addEnumValue("SECOND", "SOME_VALUE", 0);
		instance.finishInitialization();
		assertArrayEquals(new String[] {"valueOfFirst"}, instance.getAllValuesOfLabel("FIRST").toArray(new String[] {}));
		assertArrayEquals(new String[] {"SOME_VALUE"}, instance.getAllValuesOfLabel("SECOND").toArray(new String[] {}));
	}

	@Test
	public void labelNamesAreSorted() {
		instance.addValue("A", "value");
		instance.addValue("C", "value");
		instance.addValue("B", "value");
		instance.finishInitialization();
		assertArrayEquals(new String[] {"A", "B", "C"}, instance.getAllLabels().toArray(new String[] {}));
	}

	@Test
	public void nonEnumValuesAreSortedAlphabetically() {
		instance.addValue("LABEL", "A");
		instance.addValue("LABEL", "C");
		instance.addValue("LABEL", "B");
		instance.finishInitialization();
		assertArrayEquals(new String[] {"A", "B", "C"}, instance.getAllValuesOfLabel("LABEL").toArray(new String[] {}));
	}

	@Test
	public void enumValuesAreSortedByOrder() {
		instance.addEnumValue("LABEL", "A", 1);
		instance.addEnumValue("LABEL", "B", 3);
		instance.addEnumValue("LABEL", "C", 2);
		instance.finishInitialization();
		assertArrayEquals(new String[] {"A", "C", "B"}, instance.getAllValuesOfLabel("LABEL").toArray(new String[] {}));
	}

	@Test
	public void cannotMixEnumAndNonEnumValues() {
		assertThrows(ClassCastException.class, () -> {
			instance.addValue("LABEL", "value");
			instance.addEnumValue("LABEL", "enum value", 0);
			instance.finishInitialization();
		});
	}

	@Test
	public void labelValuesAreDedoubled() {
		instance.addValue("LABEL", "value");
		instance.addValue("LABEL", "value");
		instance.finishInitialization();
		assertArrayEquals(new String[] {"value"}, instance.getAllValuesOfLabel("LABEL").toArray(new String[] {}));
	}
}
