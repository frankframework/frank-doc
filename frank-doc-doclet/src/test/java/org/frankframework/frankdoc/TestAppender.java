/*
   Copyright 2020, 2021 WeAreFrank!

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
package org.frankframework.frankdoc;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.frankframework.frankdoc.util.LogUtil;

public class TestAppender extends AbstractAppender {
	private final List<String> logMessages = new ArrayList<String>();

	public static <B extends Builder<B>> B newBuilder() {
		return new Builder<B>().asBuilder().setName("jUnit-Test-Appender");
	}

	public static class Builder<B extends Builder<B>> extends AbstractAppender.Builder<B> {
		public TestAppender build() {
			return new TestAppender(getName(), getFilter(), getOrCreateLayout());
		}
	}

	private TestAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
		super(name, filter, layout, false, null);
		start();
	}

	@Override
	public void append(LogEvent logEvent) {
		logMessages.add(logEvent.getMessage().getFormattedMessage());
	}

	public int getNumberOfAlerts() {
		return logMessages.size();
	}

	public List<String> getLogLines() {
		return new ArrayList<String>(logMessages);
	}

	private static Logger getRootLogger() {
		return (Logger) LogUtil.getRootLogger();
	}

	public static void addToRootLogger(TestAppender appender) {
		Logger logger = getRootLogger();
		logger.addAppender(appender);
	}

	public static void removeAppender(TestAppender appender) {
		Logger logger = getRootLogger();
		logger.removeAppender(appender);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String log : getLogLines()) {
			sb.append(log);
			sb.append("\n");
		}
		return sb.toString();
	}

	public void assertLogged(String msg) {
		assertTrue("Expect log message: " + msg, logMessages.contains(msg));
	}
}