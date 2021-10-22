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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.filter.AbstractFilterable;

@Plugin(name = "StopOnErrorAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public class StopOnErrorAppender extends AbstractAppender {
	public static boolean HAVE_ERRORS = false;

	@PluginBuilderFactory
    public static Builder<?> newBuilder() {
    	return new Builder();
    }

	public static class Builder<B extends Builder<B>> extends AbstractFilterable.Builder<B>
	implements org.apache.logging.log4j.core.util.Builder<StopOnErrorAppender> {
		@PluginBuilderAttribute
		@Required(message = "No name provided for StopOnErrorAppender")
		private String name;		

		@PluginConfiguration
		private Configuration configuration;

		@PluginElement("AppenderRef")
		@Required(message = "No delegate appender references provided to StopOnErrorAppender")
		private AppenderRef[] delegates;

		@Override
		public StopOnErrorAppender build() {
			return new StopOnErrorAppender(name, getFilter(), configuration, null, false, null, delegates);
		}

		public Builder<B> setName(String name) {
			this.name = name;
			return this;
		}

		public Builder<B> setConfiguration(final Configuration configuration) {
			this.configuration = configuration;
			return this;
		}

		public Builder<B> setDelegates(AppenderRef[] delegates) {
			this.delegates = delegates;
			return this;
		}
	}

	private AppenderRef[] delegateRefs;
	private Configuration configuration;
	private List<Appender> delegates = new ArrayList<>();
	private boolean didLog = false;

	public StopOnErrorAppender(final String name, final Filter filter, final Configuration configuration, final Layout<? extends Serializable> layout,
            final boolean ignoreExceptions, final Property[] properties, AppenderRef[] delegateRefs) {
		super(name, filter, layout, ignoreExceptions, properties);
		this.delegateRefs = delegateRefs;
		this.configuration = configuration;
		if(delegateRefs == null) {
			LOGGER.error("No delegates supplied to StopOnErrorAppender");
			return;
		}
	}

	@Override
	public void start() {
		final Map<String, Appender> map = configuration.getAppenders();
		List<String> delegateNames = Arrays.asList(delegateRefs).stream()
				.map(AppenderRef::getRef)
				.collect(Collectors.toList());
		for(String delegateName: delegateNames) {
			if(map.containsKey(delegateName)) {
				delegates.add(map.get(delegateName));
			} else {
				LOGGER.error("Unknown appender [{}]", delegateName);
			}
		}
		super.start();
	}

	@Override
	public void append(LogEvent event) {
		if(! didLog) {
			didLog = true;
		}
		delegates.forEach(d -> d.append(event));
		Level level = event.getLevel();
		if((level == Level.FATAL) || (level == Level.ERROR)) {
			HAVE_ERRORS = true;
		}
	}
}
