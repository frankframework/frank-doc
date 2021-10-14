package org.frankframework.frankdoc.model;

import static org.frankframework.frankdoc.model.ElementChild.ALL_NOT_EXCLUDED;
import static org.frankframework.frankdoc.model.ElementChild.REJECT_DEPRECATED;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;

public class NavigationUnhappyTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.walking";

	@Test
	public void whenSomeChildrenAreBothAcceptedAndRejectedThenExceptionThrown() throws IOException {
		String rootClassName = PACKAGE + "." + "GrandChild3";
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		FrankDocModel model = FrankDocModel.populate(TestUtil.resourceAsURL("doc/empty-digester-rules.xml"), rootClassName, repository);
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
			public void handleChildrenOf(FrankElement frankElement) {
			}

			@Override
			public void handleCumulativeChildrenOf(FrankElement frankElement) {
			}
		}, ALL_NOT_EXCLUDED, REJECT_DEPRECATED);
	}
}
