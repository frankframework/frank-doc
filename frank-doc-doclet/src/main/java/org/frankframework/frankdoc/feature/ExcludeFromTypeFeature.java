package org.frankframework.frankdoc.feature;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankDocException;

public final class ExcludeFromTypeFeature {
	private static Logger log = LogUtil.getLogger(ExcludeFromTypeFeature.class);
	private static final String ANNOTATION_NAME = "nl.nn.adapterframework.doc.ExcludeFromType";
	private static final String TAG_NAME = "@ff.excludeFromType";

	private static final ExcludeFromTypeFeature INSTANCE = new ExcludeFromTypeFeature();

	private ExcludeFromTypeFeature() {
	}

	public static final ExcludeFromTypeFeature getInstance() {
		return INSTANCE;
	}

	public String[] excludedFrom(FrankClass testClass) {
		FrankAnnotation annotation = testClass.getAnnotation(ANNOTATION_NAME);
		if(annotation != null) {
			try {
				return (String[]) annotation.getValue();
			} catch(FrankDocException e) {
				log.error("Class [{}] has annotation [{}], but could not parse the value", testClass.getName(), ANNOTATION_NAME, e);
			}
		} else {
			String tagValue = testClass.getJavaDocTag(TAG_NAME);
			if(tagValue != null) {
				return Arrays.asList(tagValue.split(",")).stream()
						.map(s -> s.trim())
						.collect(Collectors.toList()).toArray(new String[] {});
			}
		}
		return null;
	}
}
