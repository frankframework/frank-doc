package org.frankframework.frankdoc.model;

import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class FrankElementChildAndAncestorSelectionTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.children.ancestors.";
	private FrankDocModel model;

	@BeforeEach
	public void setUp() throws Exception {
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		model = FrankDocModel.populate(TestUtil.resourceAsURL("doc/general-test-digester-rules.xml"), PACKAGE + "Master", repository);
	}

	@Test
	public void testSelectedAttributesFetched() {
		FrankElement instance = model.findFrankElement(PACKAGE + "SelectingAncestor");
		List<FrankAttribute> children = instance.getAttributes(ElementChild.IN_XSD);
		List<String> actualNames = children.stream().map(FrankAttribute::getName).collect(Collectors.toList());
		assertArrayEquals(new String[] {"selectedAttribute"}, actualNames.toArray(new String[] {}));
	}

	@Test
	public void testSelectedConfigChildrenFetched() {
		FrankElement instance = model.findFrankElement(PACKAGE + "SelectingAncestor");
		List<ConfigChild> children = instance.getConfigChildren(ElementChild.IN_XSD);
		List<String> actualRoleNames = children.stream().map(ConfigChild::getRoleName).collect(Collectors.toList());
		assertArrayEquals(new String[] {"c"}, actualRoleNames.toArray(new String[] {}));
	}

	@Test
	public void testBothSelectingAndRejectingAncestorsConsideredForAttributes() {
		FrankElement instance = model.findFrankElement(PACKAGE + "BottomOfInheritance");
		FrankElement actualAncestor = instance.getNextAncestorThatHasOrRejectsAttributes(ElementChild.IN_XSD, ElementChild.REJECT_DEPRECATED);
		assertEquals("RejectingAncestor", actualAncestor.getSimpleName());
		actualAncestor = actualAncestor.getNextAncestorThatHasOrRejectsAttributes(ElementChild.IN_XSD, ElementChild.REJECT_DEPRECATED);
		assertEquals("SelectingAncestor", actualAncestor.getSimpleName());
		actualAncestor = actualAncestor.getNextAncestorThatHasOrRejectsAttributes(ElementChild.IN_XSD, ElementChild.REJECT_DEPRECATED);
		assertEquals("DefaultAncestor", actualAncestor.getSimpleName());
		assertNull(actualAncestor.getNextAncestorThatHasOrRejectsAttributes(ElementChild.IN_XSD, ElementChild.REJECT_DEPRECATED));
	}

	@Test
	public void testBothSelectingAndRejectingAncestorsConsideredForConfigChildren() {
		FrankElement instance = model.findFrankElement(PACKAGE + "BottomOfInheritance");
		FrankElement actualAncestor = instance.getNextAncestorThatHasOrRejectsConfigChildren(ElementChild.IN_XSD, ElementChild.REJECT_DEPRECATED);
		assertEquals("RejectingAncestor", actualAncestor.getSimpleName());
		actualAncestor = actualAncestor.getNextAncestorThatHasOrRejectsConfigChildren(ElementChild.IN_XSD, ElementChild.REJECT_DEPRECATED);
		assertEquals("SelectingAncestor", actualAncestor.getSimpleName());
		actualAncestor = actualAncestor.getNextAncestorThatHasOrRejectsConfigChildren(ElementChild.IN_XSD, ElementChild.REJECT_DEPRECATED);
		assertEquals("DefaultAncestor", actualAncestor.getSimpleName());
		assertNull(actualAncestor.getNextAncestorThatHasOrRejectsConfigChildren(ElementChild.IN_XSD, ElementChild.REJECT_DEPRECATED));
	}
}
