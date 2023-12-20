package org.frankframework.frankdoc.testtarget.featurepackage;

import org.frankframework.doc.Default;

public class ExtendsDocumented extends Documented {

	@Override
	@Default("child default value")
	public void withDefaultTag(Boolean force) {
	}

	@Override
	public void setDestinationName(String destinationName) {
		super.setDestinationName(destinationName);
	}

}
