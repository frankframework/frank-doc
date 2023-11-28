package org.frankframework.frankdoc.testtarget.packageprivate.override;

import nl.nn.adapterframework.doc.Default;
import nl.nn.adapterframework.doc.Mandatory;

public class Child extends JmsListener {

	public String ownField;
	@Override
	public void setAlarm(boolean b) {
	}

	@Mandatory
	@Override
	public void setDestinationName(String destinationName) {
		super.setDestinationName(destinationName);
	}

	@Override
	@Default("Text from the Default-tag in Child.")
	public void setForceMessageIdAsCorrelationId(Boolean force) {
		super.setForceMessageIdAsCorrelationId(force);
	}

	public String getOwnField() {
		return ownField;
	}
}
