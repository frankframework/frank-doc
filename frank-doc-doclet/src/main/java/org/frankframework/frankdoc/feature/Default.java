package org.frankframework.frankdoc.feature;

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankMethod;

public class Default {
	private static final String ANNOTATION_DEFAULT = "nl.nn.adapterframework.doc.Default";
	private static final String TAG_DEFAULT = "@ff.default";

	private static Logger log = LogUtil.getLogger(Default.class);
	private static final Default INSTANCE = new Default();

	public static Default getInstance() {
		return INSTANCE;
	}

	private Default() {
	}

	public String valueOf(FrankMethod method) {
		String result = method.getJavaDocTag(TAG_DEFAULT);
		if(result == null) {
			result = fromDefaultAnnotation(method);
		}
		if(result == null) {
			result = fromIbisDocAnnotation(method);
		}
		return result;
	}

	private String fromDefaultAnnotation(FrankMethod method) {
		FrankAnnotation annotation = method.getAnnotation(ANNOTATION_DEFAULT);
		if(annotation == null) {
			return null;
		}
		try {
			return annotation.getValue().toString();
		} catch(FrankDocException e) {
			log.error("Failed to parse annotation [{}] on method [{}]", ANNOTATION_DEFAULT, method.toString());
			return null;
		}
	}

	private String fromIbisDocAnnotation(FrankMethod method) {
		FrankAnnotation ibisDocAnnotation = method.getAnnotation(ParsedIbisDocAnnotation.IBISDOC);
		if(ibisDocAnnotation == null) {
			return null;
		}
		ParsedIbisDocAnnotation ibisDoc = null;
		try {
			ibisDoc = new ParsedIbisDocAnnotation(ibisDocAnnotation);
			return ibisDoc.getDefaultValue();
		} catch(FrankDocException e) {
			log.error("Failed to parse annotation [{}] on method [{}]", ParsedIbisDocAnnotation.IBISDOC, method.toString());
			return null;
		}
	}
}
