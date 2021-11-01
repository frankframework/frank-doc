/* 
Copyright 2021 WeAreFrank! 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/

package org.frankframework.frankdoc.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(name = "ErrorDetectingAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class ErrorDetectingAppender extends AbstractAppender {
	public static boolean HAVE_ERRORS = false;

	@PluginFactory
	public static ErrorDetectingAppender createAppender(@PluginAttribute("name") String name) {
		return new ErrorDetectingAppender(name);
	}

	private ErrorDetectingAppender(final String name) {
		super(name, null, null, false, Property.EMPTY_ARRAY);
	}

	@Override
	public void append(LogEvent event) {
		Level level = event.getLevel();
		if ((level == Level.FATAL) || (level == Level.ERROR)) {
			HAVE_ERRORS = true;
		}
	}
}
