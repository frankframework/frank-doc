package org.frankframework.frankdoc;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import lombok.Getter;

public class ErrorHandler extends DefaultHandler {
	private @Getter boolean error = false;
	private @Getter String errorString = "";

	@Override
	public void fatalError(SAXParseException e) {
		handle(e);
	}

	@Override
	public void error(SAXParseException e) {
		handle(e);
	}

	@Override
	public void warning(SAXParseException e) {
		handle(e);
	}

	private void handle(SAXParseException e) {
		error = true;
		if(StringUtils.isBlank(errorString)) {
			errorString = e.toString();
		}
	}
}
