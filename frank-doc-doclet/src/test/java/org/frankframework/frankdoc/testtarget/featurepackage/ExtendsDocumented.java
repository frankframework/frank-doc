package org.frankframework.frankdoc.testtarget.featurepackage;

import nl.nn.adapterframework.doc.Default;

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
