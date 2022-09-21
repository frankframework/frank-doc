package org.frankframework.frankdoc.wrapper;

import org.junit.Before;

class TestBase {
	static final String PACKAGE = "org.frankframework.frankdoc.testtarget.doclet.";

	FrankClassRepository classRepository;

	@Before
	public void setUp() {
		classRepository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
	}
}
