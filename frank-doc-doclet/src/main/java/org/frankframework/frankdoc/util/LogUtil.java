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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Logger factory. Loggers are created using this factory instead of using {@link LogManager} directly.
 * This way, we have the option of changing the logging environment later.
 */
public class LogUtil {
	public static Logger getLogger(Class<?> clazz) {
		return LogManager.getLogger(clazz);
	}

	public static Logger getRootLogger() {
		return LogManager.getRootLogger();
	}
}
