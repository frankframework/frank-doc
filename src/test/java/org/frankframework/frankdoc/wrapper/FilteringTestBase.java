package org.frankframework.frankdoc.wrapper;

class FilteringTestBase {
	static final String PREFIX = "org.frankframework.frankdoc.testtarget.doclet.filtering.";
	static final String FIRST = "first.";
	static final String SECOND = "second.";
	static final String FIRST_PACKAGE = PREFIX + FIRST;
	static final String SECOND_PACKAGE = PREFIX + SECOND;
	static final String[] BOTH_PACKAGES = new String[] {FIRST_PACKAGE, SECOND_PACKAGE};
	static final String FIRST_IMPL = "FirstImpl";
	static final String CHILD_OF_FIRST_IMPL_IN_SECOND_PACKAGE = "ChildOfFirstImplInOtherPackage";
	static final String SECOND_IMPL = "SecondImpl";
	static final String THIRD_IMPL = "ThirdImpl";
	static final String[] NO_EXCLUDES = new String[] {};
	static final String[] THIRD_IMPL_EXCLUDED = new String[] {SECOND_PACKAGE + THIRD_IMPL};
	static final String[] CHILD_OF_FIRST_IMPL_IN_SECOND_PACKAGE_EXCLUDED = new String[] {SECOND_PACKAGE + CHILD_OF_FIRST_IMPL_IN_SECOND_PACKAGE};
}
