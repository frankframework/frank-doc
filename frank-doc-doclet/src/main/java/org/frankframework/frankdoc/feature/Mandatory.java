package org.frankframework.frankdoc.feature;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.Constants;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankMethod;

public class Mandatory {
	public enum Value {
		IGNORE_COMPATIBILITY,
		DONT_IGNORE_COMPATIBILITY;
	}

	private static final String TAG_NAME = Constants.JAVA_DOC_TAG_MANDATORY;
	private static final String ANNOTATION_NAME = "nl.nn.adapterframework.doc.Mandatory";

	private static final String IGNORE_COMPATIBILITY_MODE = "ignoreInCompatibilityMode";

	private static final Mandatory INSTANCE = new Mandatory();
	private static Logger log = LogUtil.getLogger(Mandatory.class);

	public static Mandatory getInstance() {
		return INSTANCE;
	}

	private Mandatory() {
	}

	public Value valueOf(FrankMethod method) {
		String tagValue = method.getJavaDocTag(TAG_NAME);
		if(tagValue != null) {
			if(StringUtils.isBlank(tagValue)) {
				return Value.DONT_IGNORE_COMPATIBILITY;
			} else if(tagValue.equals(IGNORE_COMPATIBILITY_MODE)) {
				return Value.IGNORE_COMPATIBILITY;
			} else {
				log.error("Method [{}] has JavaDoc tag [{}] with invalid value [{}]", method.toString(), TAG_NAME, tagValue);
				return Value.DONT_IGNORE_COMPATIBILITY;
			}
		}
		FrankAnnotation annotation = method.getAnnotation(ANNOTATION_NAME);
		if(annotation != null) {
			boolean annotationValue = false;
			try {
				annotationValue = (Boolean) annotation.getValueOf("ignoreInCompatibilityMode");
			} catch(FrankDocException e) {
				log.error("Method [{}] has Java annotation [{}] but cannot get value", method.toString(), ANNOTATION_NAME);
			}
			if(annotationValue) {
				return Value.IGNORE_COMPATIBILITY;
			} else {
				return Value.DONT_IGNORE_COMPATIBILITY;
			}
		}
		return null;
	}
}
