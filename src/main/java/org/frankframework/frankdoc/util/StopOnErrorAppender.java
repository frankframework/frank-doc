package org.frankframework.frankdoc.util;

import java.io.Serializable;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "StopOnErrorAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class StopOnErrorAppender extends AbstractAppender {

	public StopOnErrorAppender(final String name, final Filter filter, final Layout<? extends Serializable> layout,
            final boolean ignoreExceptions, final Property[] properties) {
		super(name, filter, layout, ignoreExceptions, properties);
	}

	@Override
	public void append(LogEvent event) {
		// TODO Auto-generated method stub
		
	}

}
