package org.frankframework.frankdoc.feature;

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankMethod;

public class Description {
	private static Logger log = LogUtil.getLogger(Description.class);
	private static final Description INSTANCE = new Description();

	public static Description getInstance() {
		return INSTANCE;
	}

	private Description() {
	}

	public String valueOf(FrankMethod method) {
		String result = method.getJavaDoc();
		if(result == null) {
			FrankAnnotation annotation = method.getAnnotation(ParsedIbisDocAnnotation.IBISDOC);
			if(annotation != null) {
				try {
					ParsedIbisDocAnnotation ibisDoc = new ParsedIbisDocAnnotation(annotation);
					result = ibisDoc.getDescription();
				} catch(FrankDocException e) {
					log.error("Could not parse annotation [{}] on method [{}]", ParsedIbisDocAnnotation.IBISDOC, method.toString());
				}
			}
		}
		return result;
	}

	public String valueOf(FrankClass clazz) {
		return clazz.getJavaDoc();
	}
}
