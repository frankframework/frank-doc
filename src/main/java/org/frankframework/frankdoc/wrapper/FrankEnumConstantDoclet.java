/* 
Copyright 2021, 2022 WeAreFrank! 

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

package org.frankframework.frankdoc.wrapper;

import java.util.Map;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.Tag;

import lombok.Getter;

class FrankEnumConstantDoclet implements FrankEnumConstant {
	private FieldDoc fieldDoc;
	private @Getter String name;
	private boolean isPublic;
	private @Getter String javaDoc;
	private Map<String, FrankAnnotation> annotationsByName;

	FrankEnumConstantDoclet(FieldDoc fieldDoc) {
		this.fieldDoc = fieldDoc;
		this.name = fieldDoc.name();
		this.isPublic = fieldDoc.isPublic();
		this.javaDoc = fieldDoc.commentText();
		AnnotationDesc[] javaDocAnnotations = fieldDoc.annotations();
		annotationsByName = FrankDocletUtils.getFrankAnnotationsByName(javaDocAnnotations);
	}
	
	@Override
	public boolean isPublic() {
		return this.isPublic;
	}

	@Override
	public FrankAnnotation getAnnotation(String name) {
		return annotationsByName.get(name);
	}

	@Override
	public String getJavaDocTag(String tagName) {
		Tag[] tags = fieldDoc.tags(tagName);
		if((tags == null) || (tags.length == 0)) {
			return null;
		}
		// The Doclet API trims the value.
		return tags[0].text();
	}
}
