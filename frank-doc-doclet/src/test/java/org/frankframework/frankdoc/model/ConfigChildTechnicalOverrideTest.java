package org.frankframework.frankdoc.model;

import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigChildTechnicalOverrideTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.technical.override.";
	private static final String DIGESTER_RULES = "doc/technical-override-digester-rules.xml";

	private static FrankDocModel model;

	@BeforeAll
	public static void setUp() throws IOException {
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE, FRANK_DOC_GROUP_VALUES_PACKAGE);
		model = FrankDocModel.populate(TestUtil.resourceAsURL(DIGESTER_RULES), null, PACKAGE + "Master", repository);
	}

	@Test
	public void whenConfigChildNotInheritedThenNoTechnicalOverride() {
		FrankElement element = model.findFrankElement(PACKAGE + "ChildTechnicalOverride");
		ConfigChild child = selectChildByArgumentType(element, "ChildTechnicalOverride");
		assertFalse(child.isTechnicalOverride());
	}

	private ConfigChild selectChildByArgumentType(FrankElement element, String simpleNameArgumentType) {
		List<ConfigChild> children = element.getConfigChildren(ElementChild.ALL_NOT_EXCLUDED).stream()
				.filter(c -> c instanceof ObjectConfigChild)
				.map(c -> (ObjectConfigChild) c)
				.filter(c -> c.getElementType().getFullName().equals(PACKAGE + simpleNameArgumentType))
				.collect(Collectors.toList());
		assertEquals(1, children.size());
		ConfigChild child = children.get(0);
		return child;
	}

	@Test
	public void whenConfigChildOverriddenThenTechnicalOverride() {
		FrankElement element = model.findFrankElement(PACKAGE + "ChildTechnicalOverride");
		ConfigChild child = selectChildByArgumentType(element, "Master");
		assertTrue(child.isTechnicalOverride());
	}

	@Test
	public void whenConfigChildInheritedWithoutJavaOverrideThenNoTechnicalOverride() {
		FrankElement element = model.findFrankElement(PACKAGE + "ChildMeaningfulOverride");
		FrankElement superElement = model.findFrankElement(PACKAGE + "ParentMeaningfulOverride");
		ConfigChild child = selectChildByArgumentType(element, "Master");
		assertFalse(child.isTechnicalOverride());
		assertEquals(superElement.getSimpleName(), child.getOverriddenFrom().getSimpleName());
		assertTrue(child.isAllowMultiple());
		ConfigChild inherited = selectChildByArgumentType(superElement, "Master");
		assertFalse(inherited.isTechnicalOverride());
		assertFalse(inherited.isAllowMultiple());
	}
}
