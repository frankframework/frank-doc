package org.frankframework.frankdoc.model;

import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FrankDocModelDocletTest {
	private static final String SIMPLE = "org.frankframework.frankdoc.testtarget.simple.";
	private static final String EXPECTED_DESCRIPTION =
			"The JavaDoc comment of class \"Container\".\n" +
			"\n" +
			" This is additional text that we do not add to the XSDs or the Frank!Doc website.";
	private static final String EXPECTED_DESCRIPTION_HEADER = "The JavaDoc comment of class \"Container\".";

	private FrankDocModel instance;

	@BeforeEach
	public void setUp() throws IOException {
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(SIMPLE, FRANK_DOC_GROUP_VALUES_PACKAGE);
		instance = FrankDocModel.populate(TestUtil.resourceAsURL("doc/xsd-element-name-digester-rules.xml"), null, SIMPLE + "Container", repository);
	}

	@Test
	public void whenClassHasJavadocThenInFrankElementDescription() {
		FrankElement frankElement = instance.findFrankElement(SIMPLE + "Container");
		assertEquals(EXPECTED_DESCRIPTION, frankElement.getDescription());
		assertEquals(EXPECTED_DESCRIPTION_HEADER, frankElement.getDescriptionHeader());
	}
}
