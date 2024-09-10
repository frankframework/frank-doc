package org.frankframework.frankdoc.model;

import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.frankframework.frankdoc.Constants.FRANK_DOC_GROUP_VALUES_PACKAGE;
import static org.frankframework.frankdoc.model.ElementChild.ALL_NOT_EXCLUDED;
import static org.frankframework.frankdoc.model.ElementChild.REJECT_DEPRECATED;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NavigationUnhappyTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.walking";

	@Test
	public void whenSomeChildrenAreBothAcceptedAndRejectedThenExceptionThrown() throws IOException {
		String rootClassName = PACKAGE + "." + "GrandChild3";
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE, FRANK_DOC_GROUP_VALUES_PACKAGE);
		FrankDocModel model = FrankDocModel.populate(TestUtil.resourceAsURL("doc/empty-digester-rules.xml"), null, rootClassName, repository);
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> walk(rootClassName, model));
		assertTrue(e.getMessage().contains("[parentAttributeSecond]"));
	}

	private void walk(String rootClassName, FrankDocModel model) {
		FrankElement walkFrom = model.findFrankElement(rootClassName);
		walkFrom.walkCumulativeAttributes(new CumulativeChildHandler<FrankAttribute>() {
			@Override
			public void handleSelectedChildren(List<FrankAttribute> children, FrankElement owner) {
			}

			@Override
			public void handleSelectedChildrenOfTopLevel(List<FrankAttribute> children, FrankElement owner) {
			}

			@Override
			public void handleChildrenOf(FrankElement frankElement) {
			}

			@Override
			public void handleCumulativeChildrenOf(FrankElement frankElement) {
			}
		}, ALL_NOT_EXCLUDED, REJECT_DEPRECATED);
	}
}
