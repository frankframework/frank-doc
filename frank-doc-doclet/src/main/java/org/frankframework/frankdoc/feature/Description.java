/* 
Copyright 2022 WeAreFrank! 

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

package org.frankframework.frankdoc.feature;

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankAnnotation;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankDocletConstants;
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
		String result = null;
		FrankAnnotation annotation = method.getAnnotation(FrankDocletConstants.IBISDOC);
		if(annotation != null) {
			try {
				ParsedIbisDocAnnotation ibisDoc = new ParsedIbisDocAnnotation(annotation);
				result = ibisDoc.getDescription();
			} catch(FrankDocException e) {
				log.error("Could not parse annotation [{}] on method [{}]", FrankDocletConstants.IBISDOC, method.toString());
			}
		}
		if(result == null) {
			result = method.getJavaDoc();
		}
		return result;
	}

	public String valueOf(FrankClass clazz) {
		return clazz.getJavaDoc();
	}
}
