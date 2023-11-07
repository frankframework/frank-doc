package org.frankframework.frankdoc.testtarget.doclet.interfaces;

public class DiamondImplementationOfCommonParent<M> implements FirstChildOfCommonParent<M>, SecondChildOfCommonParent {
	@Override
	public void annotatedMethod(String value) {
	}
}
