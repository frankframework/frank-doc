package org.frankframework.frankdoc.model;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class FrankDocModelDocletTest {
	private static final String SIMPLE = "org.frankframework.frankdoc.testtarget.simple.";
	private static final String CONFIGURATION_FILTERS = "org.frankframework.configuration.filters.";
	private static final String EXPECTED_DESCRIPTION =
			"The JavaDoc comment of class \"Container\".\n" +
			"\n" +
			"This is additional text that we do not add to the XSDs or the Frank!Doc website.";
	private static final String EXPECTED_DESCRIPTION_HEADER = "The JavaDoc comment of class \"Container\".";

	private FrankDocModel instance;

	@BeforeEach
	public void setUp() {
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(SIMPLE, FRANK_DOC_GROUP_VALUES_PACKAGE, CONFIGURATION_FILTERS);
		instance = FrankDocModel.populate(TestUtil.resourceAsURL("doc/xsd-element-name-digester-rules.xml"), null, SIMPLE + "Container", repository);
	}

	@Test
	public void whenClassHasJavadocThenInFrankElementDescription() {
		FrankElement frankElement = instance.findFrankElement(SIMPLE + "Container");
		assertEquals(EXPECTED_DESCRIPTION, frankElement.getDescription());
		assertEquals(EXPECTED_DESCRIPTION_HEADER, frankElement.getDescriptionHeader());
	}

	@Test
	public void whenClassHasSkippableContainerElements() {
		Set<String> skippableContainerElements = instance.getSkippableContainers();
		assertEquals(2, skippableContainerElements.size());
//		assertEquals(SIMPLE + "Child", frankElement.getChildren().get(0).getName());
	}
}
