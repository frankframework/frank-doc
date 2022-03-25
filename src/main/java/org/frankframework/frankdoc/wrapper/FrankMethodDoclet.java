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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;

class FrankMethodDoclet extends FrankMethodDocletBase {
	private static Logger log = LogUtil.getLogger(FrankMethodDoclet.class);

	final MethodDoc method;
	private final Map<String, FrankAnnotation> frankAnnotationsByName;

	FrankMethodDoclet(MethodDoc method, FrankClassDoclet declaringClass) {
		super(declaringClass);
		this.method = method;
		AnnotationDesc[] annotationDescs = method.annotations();
		frankAnnotationsByName = FrankDocletUtils.getFrankAnnotationsByName(annotationDescs);
	}

	@Override
	public boolean isMultiplyEnheritedPlaceholder() {
		return false;
	}

	@Override
	public String getName() {
		return method.name();
	}

	@Override
	public boolean isPublic() {
		return method.isPublic();
	}

	@Override
	public FrankAnnotation[] getAnnotations() {
		return frankAnnotationsByName.values().toArray(new FrankAnnotation[] {});
	}

	@Override
	public FrankAnnotation getAnnotation(String name) {
		return frankAnnotationsByName.get(name);
	}

	@Override
	public String getJavaDoc() {
		String result = method.commentText();
		// We need null when there is no JavaDoc, not the empty string.
		// We use the null result in getJavaDocIncludingInherited() to
		// continue searching.
		if(StringUtils.isBlank(result)) {
			return null;
		}
		return result;
	}

	@Override
	public FrankType getReturnType() {
		Type docletType = method.returnType();
		return typeOf(docletType);
	}

	private FrankType typeOf(Type docletType) {
		if(docletType.isPrimitive()) {
			return new FrankPrimitiveType(docletType.simpleTypeName());
		} else {
			String typeName = docletType.qualifiedTypeName();
			try {
				FrankClass clazz = ((FrankClassDoclet) getDeclaringClass()).getRepository().findClass(typeName);
				if(clazz == null) {
					return new FrankNonCompiledClassDoclet(typeName);
				} else {
					return clazz;
				}
			} catch(FrankDocException e) {
				log.error("Failed to search for class with name {}", typeName, e);
				return new FrankNonCompiledClassDoclet(typeName);
			}
		}
	}

	@Override
	public int getParameterCount() {
		return method.parameters().length;
	}

	@Override
	public boolean isVarargs() {
		return method.isVarArgs();
	}

	@Override
	public FrankType[] getParameterTypes() {
		Parameter[] parametersDoclet = method.parameters();
		FrankType[] result = new FrankType[parametersDoclet.length];
		for(int i = 0; i < parametersDoclet.length; ++i) {
			result[i] = typeOf(parametersDoclet[i].type());
		}
		return result;
	}

	@Override
	public String getSignature() {
		List<String> components = new ArrayList<>();
		components.add(getName());
		for(FrankType type: getParameterTypes()) {
			components.add(type.getName());
		}
		return components.stream().collect(Collectors.joining(", "));
	}

	void removeOverriddenFrom(Map<MethodDoc, FrankMethod> methodRepository) {
		MethodDoc toRemove = method.overriddenMethod();
		methodRepository.remove(toRemove);
	}

	void addToRepository(Map<MethodDoc, FrankMethod> methodRepository) {
		methodRepository.put(method, this);
	}

	@Override
	public String getJavaDocTag(String tagName) {
		Tag[] tags = method.tags(tagName);
		if((tags == null) || (tags.length == 0)) {
			return null;
		}
		// The Doclet API trims the value.
		return tags[0].text();
	}

	@Override
	public String toString() {
		return toStringImpl();
	}

	@Override
	MethodDoc getOverriddenMethodDoc() {
		return method.overriddenMethod();
	}
}
