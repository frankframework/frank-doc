package org.frankframework.frankdoc.wrapper;

import org.junit.jupiter.api.BeforeEach;

class TestBase {
	static final String PACKAGE = "org.frankframework.frankdoc.testtarget.doclet.";

	FrankClassRepository classRepository;

	@BeforeEach
	public void setUp() {
		classRepository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
	}
}
