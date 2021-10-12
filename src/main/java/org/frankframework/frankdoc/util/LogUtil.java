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
}
