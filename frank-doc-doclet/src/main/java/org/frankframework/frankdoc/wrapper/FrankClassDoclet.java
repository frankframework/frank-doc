/* 
Copyright 2021 -  2023 WeAreFrank! 

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;

import lombok.AccessLevel;
import lombok.Getter;

class FrankClassDoclet implements FrankClass {
	private static Logger log = LogUtil.getLogger(FrankClassDoclet.class);

	private final FrankClassRepository repository;
	private final ClassDoc clazz;
	private final Set<String> childClassNames = new HashSet<>();
	private final Map<String, FrankClass> interfaceImplementationsByName = new HashMap<>();
	private final LinkedHashMap<MethodDoc, FrankMethod> frankMethodsByDocletMethod = new LinkedHashMap<>();
	private final Map<String, FrankMethodDoclet> methodsBySignature = new HashMap<>();
	private final Map<String, FrankAnnotation> frankAnnotationsByName;
	private @Getter(AccessLevel.PACKAGE) List<MultiplyInheritedMethodPlaceholder> multiplyInheritedMethodPlaceholders = new ArrayList<>();
	private Map<String, String> variables = new HashMap<>();

	FrankClassDoclet(ClassDoc clazz, FrankClassRepository repository) {
		log.trace("Creating FrankClassDoclet for [{}]", clazz.name());
		this.repository = repository;
		this.clazz = clazz;
		for(MethodDoc methodDoc: clazz.methods()) {
			FrankMethodDoclet frankMethod = new FrankMethodDoclet(methodDoc, this);
			frankMethodsByDocletMethod.put(methodDoc, frankMethod);
			methodsBySignature.put(frankMethod.getSignature(), frankMethod);
		}
		AnnotationDesc[] annotationDescs = clazz.annotations();
		frankAnnotationsByName = FrankDocletUtils.getFrankAnnotationsByName(annotationDescs);
		initializeVariables();
	}

	private void initializeVariables() {
		for(FieldDoc fieldDoc: clazz.fields()) {
			Object value = fieldDoc.constantValue();
			if(value != null) {
				variables.put(fieldDoc.name(), value.toString());
			}
		}
		for(FieldDoc fieldDoc: clazz.enumConstants()) {
			variables.put(fieldDoc.name(), fieldDoc.name());
		}
	}

	void addChild(String className) {
		childClassNames.add(className);
	}

	void recursivelyAddInterfaceImplementation(FrankClassDoclet implementation) throws FrankDocException {
		if(((FrankClassRepositoryDoclet) repository).classIsAllowedAsInterfaceImplementation(implementation)) {
			log.trace("Interface {} is implemented by {}", () -> getName(), () -> implementation.getName());
			// TODO: Test that children of omitted classes can be accepted again.
			interfaceImplementationsByName.put(implementation.getName(), implementation);
		} else {
			log.trace("From interface {} omitted implementation because of filtering {}", () -> getName(), () -> implementation.getName());			
		}
		for(String implementationChildClassName: implementation.childClassNames) {
			FrankClassDoclet implementationChild = (FrankClassDoclet) repository.findClass(implementationChildClassName);
			recursivelyAddInterfaceImplementation(implementationChild);
		}
	}

	@Override
	public boolean isEnum() {
		return clazz.isEnum();
	}

	@Override
	public String getName() {
		return clazz.qualifiedName();
	}

	@Override
	public FrankAnnotation[] getAnnotations() {
		List<FrankAnnotation> values = new ArrayList<>(frankAnnotationsByName.values());
		return values.toArray(new FrankAnnotation[] {});
	}

	@Override
	public FrankAnnotation getAnnotation(String name) {
		return frankAnnotationsByName.get(name);
	}

	@Override
	public String getSimpleName() {
		String result = clazz.name();
		// For inner classes, we now have result == <outerClassName>.<innerClassName>
		// We want only <innerClassName>
		if(result.contains(".")) {
			result = result.substring(result.lastIndexOf(".") + 1);
		}
		return result;
	}

	@Override
	public String getPackageName() {
		return clazz.containingPackage().name();
	}

	@Override
	public FrankClass getSuperclass() {
		FrankClass result = null;
		ClassDoc superClazz = clazz.superclass();
		if(superClazz != null) {
			try {
				String superclassQualifiedName = superClazz.qualifiedName();
				boolean omit = ((FrankClassRepositoryDoclet) repository).getExcludeFiltersForSuperclass().stream().anyMatch(
						exclude -> superclassQualifiedName.startsWith(exclude));
				if(omit) {
					return null;
				}
				result = repository.findClass(superclassQualifiedName);
			} catch(FrankDocException e) {
				log.error("Could not get superclass of {}", getName(), e);
			}
		}
		return result;
	}

	@Override
	public FrankClass[] getInterfaces() {
		List<FrankClass> resultList = getInterfacesAsList();
		return resultList.toArray(new FrankClass[] {});
	}

	List<FrankClass> getInterfacesAsList() {
		ClassDoc[] interfaceDocs = clazz.interfaces();
		List<FrankClass> resultList = new ArrayList<>();
		for(ClassDoc interfaceDoc: interfaceDocs) {
			try {
				FrankClass interfaze = repository.findClass(interfaceDoc.qualifiedName());
				if(interfaze != null) {
					resultList.add(interfaze);
				}
			} catch(FrankDocException e) {
				log.error("Error searching for {}", interfaceDoc.name(), e);
			}
		}
		return resultList;
	}

	@Override
	public boolean isAbstract() {
		return clazz.isAbstract();
	}

	@Override
	public boolean isInterface() {
		return clazz.isInterface();
	}

	@Override
	public boolean isPublic() {
		return clazz.isPublic();
	}

	@Override
	public List<FrankClass> getInterfaceImplementations() throws FrankDocException {
		if(! isInterface()) {
			throw new FrankDocException(String.format("Cannot get implementations of non-interface [%s]", getName()), null);
		}
		return interfaceImplementationsByName.values().stream()
				// Remove abstract classes to make it the same as reflection does it.
				.filter(c -> ! c.isAbstract())
				.collect(Collectors.toList());
	}

	@Override
	public FrankMethod[] getDeclaredMethods() {
		List<FrankMethod> resultList = new ArrayList<>(frankMethodsByDocletMethod.values());
		return resultList.toArray(new FrankMethod[] {});
	}

	@Override
	public FrankMethod[] getDeclaredAndInheritedMethods() {
		List<FrankMethod> resultList = getDeclaredAndInheritedMethodsAsMap().values().stream()
				.filter(FrankMethod::isPublic)				
				.collect(Collectors.toList());
		FrankMethod[] result = new FrankMethod[resultList.size()];
		for(int i = 0; i < resultList.size(); ++i) {
			result[i] = resultList.get(i);
		}
		return result;
	}

	private Map<MethodDoc, FrankMethod> getDeclaredAndInheritedMethodsAsMap() {
		final Map<MethodDoc, FrankMethod> result = new HashMap<>();
		if(getSuperclass() != null) {
			result.putAll(((FrankClassDoclet) getSuperclass()).getDeclaredAndInheritedMethodsAsMap());
		}
		List<FrankMethod> declaredMethodList = Arrays.asList(getDeclaredMethods());
		for(FrankMethod declaredMethod: declaredMethodList) {
			((FrankMethodDoclet) declaredMethod).removeOverriddenFrom(result);
		}
		declaredMethodList.forEach(dm -> ((FrankMethodDoclet) dm).addToRepository(result));
		return result;
	}

	@Override
	public FrankEnumConstant[] getEnumConstants() {
		FieldDoc[] fieldDocs = clazz.enumConstants();
		FrankEnumConstant[] result = new FrankEnumConstant[fieldDocs.length];
		for(int i = 0; i < fieldDocs.length; ++i) {
			result[i] = new FrankEnumConstantDoclet(fieldDocs[i]);
		}
		return result;
	}

	FrankClassRepositoryDoclet getRepository() {
		return (FrankClassRepositoryDoclet) repository;
	}

	FrankMethod recursivelyFindFrankMethod(MethodDoc methodDoc) {
		if(frankMethodsByDocletMethod.containsKey(methodDoc)) {
			return frankMethodsByDocletMethod.get(methodDoc);
		} else if(getSuperclass() != null) {
			return ((FrankClassDoclet) getSuperclass()).recursivelyFindFrankMethod(methodDoc);
		} else {
			return null;
		}
	}

	FrankMethodDoclet getMethodFromSignature(String signature) {
		return methodsBySignature.get(signature);
	}

	<T> T getMethodItemFromSignature(String methodSignature, Function<FrankMethodDocletBase, T> getter) {
		FrankMethodDoclet frankMethod = getMethodFromSignature(methodSignature);
		if(frankMethod != null) {
			return getter.apply((FrankMethodDocletBase) frankMethod);
		}
		return null;
	}

	boolean isTopLevel() {
		return clazz.containingClass() == null;
	}

	@Override
	public String getJavaDoc() {
		return clazz.commentText();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public FrankAnnotation getAnnotationIncludingInherited(String annotationFullName) throws FrankDocException {
		Function<FrankClassDoclet, FrankAnnotation> getter = c -> c.getAnnotation(annotationFullName);
		return getIncludingInherited(getter);
	}

	private <T> T getIncludingInherited(Function<FrankClassDoclet, T> getter) throws FrankDocException {
		T result = getExcludingImplementedInterfaces(getter);
		if(result == null) {
			result = getFromImplementedInterfaces(getter);
		}
		return result;
	}

	private <T> T getExcludingImplementedInterfaces(Function<FrankClassDoclet, T> getter) throws FrankDocException {
		T result = getter.apply(this);
		if((result == null) && (getSuperclass() != null)) {
			result = ((FrankClassDoclet) getSuperclass()).getExcludingImplementedInterfaces(getter);
		}
		return result;
	}

	private <T> T getFromImplementedInterfaces(Function<FrankClassDoclet, T> getter) throws FrankDocException {
		TransitiveImplementedInterfaceBrowser<T> browser = new TransitiveImplementedInterfaceBrowser<>(this);
		T result = browser.search(c -> getter.apply((FrankClassDoclet) c));
		if((result == null) && (getSuperclass() != null)) {
			result = ((FrankClassDoclet) getSuperclass()).getFromImplementedInterfaces(getter);
		}
		return result;
	}

	@Override
	public void browseAncestors(Consumer<FrankClass> handler) throws FrankDocException {
		Function<FrankClassDoclet, Boolean> getter = c -> adapt(c, handler);
		getIncludingInherited(getter);
	}

	private Boolean adapt(FrankClassDoclet c, Consumer<FrankClass> handler) {
		handler.accept(c);
		return null;
	}
	
	@Override
	public String getJavaDocTag(String tagName) {
		Tag[] tags = clazz.tags(tagName);
		if((tags == null) || (tags.length == 0)) {
			return null;
		}
		// The Doclet API trims the value.
		return tags[0].text();
	}

	@Override
	public List<String> getAllJavaDocTagsOf(String tagName) {
		Tag[] tags = clazz.tags(tagName);
		if(tags == null) {
			return new ArrayList<>();
		}
		return Arrays.asList(tags).stream().map(Tag::text).collect(Collectors.toList());		
	}

	@Override
	public String getJavaDocTagIncludingInherited(String tagName) throws FrankDocException {
		Function<FrankClassDoclet, String> getter = c -> c.getJavaDocTag(tagName);
		return getIncludingInherited(getter);
	}

	@Override
	public FrankMethod[] getDeclaredMethodsAndMultiplyInheritedPlaceholders() {
		List<FrankMethod> result = new ArrayList<>();
		result.addAll(frankMethodsByDocletMethod.values());
		result.addAll(multiplyInheritedMethodPlaceholders);
		return result.toArray(new FrankMethod[] {});
	}

	void addMultiplyInheritedMethodPlaceholders() {
		try {
			List<FrankClass> interfaces = new TransitiveImplementedInterfaceBrowser<Object>(this).getInterfacesAndTheirAncestors();
			if(! interfaces.isEmpty()) {
				multiplyInheritedMethodPlaceholders = getReinheritedMethods(interfaces).stream()
						.map(m -> (FrankMethodDoclet) m)
						.map(m -> new MultiplyInheritedMethodPlaceholder(m, this))
						.collect(Collectors.toList());
				if(log.isTraceEnabled()) {
					String placeholderMethodNames = multiplyInheritedMethodPlaceholders.stream()
							.map(FrankMethod::getName).collect(Collectors.joining(", "));
					log.trace("Class [{}] has multiply inherited methods: [{}]", getName(), placeholderMethodNames);
				}
			}
		} catch(FrankDocException e) {
			log.error("Failed to add multiply inherited method placeholders for FrankClass [{}]", getName(), e);
		}
	}

	List<FrankMethod> getReinheritedMethods(List<FrankClass> interfaces) {
		List<FrankMethod> result = new ArrayList<>();
		Set<String> declaredMethodSignatures = Arrays.asList(getDeclaredMethods()).stream()
				.map(FrankMethod::getSignature)
				.collect(Collectors.toSet());
		Set<String> methodSignaturesFromImplementedInterfaces = interfaces.stream()
				.flatMap(FrankClassDoclet::methodSignaturesOf)
				.collect(Collectors.toSet());
		for(FrankMethod candidate: getDeclaredAndInheritedMethods()) {
			String candidateSignature = candidate.getSignature();
			boolean notDeclared = ! declaredMethodSignatures.contains(candidateSignature);
			boolean reinherited = methodSignaturesFromImplementedInterfaces.contains(candidateSignature);
			if(notDeclared && reinherited) {
				result.add(candidate);
			}
		}
		return result;
	}

	private static Stream<String> methodSignaturesOf(FrankClass intf) {
		return Arrays.asList(intf.getDeclaredMethods()).stream().map(FrankMethod::getSignature);
	}

	@Override
	public String resolveValue(String variable) {
		return variables.get(variable);
	}

	@Override
	public FrankClass findClass(String name) {
		ClassDoc foundClassDoc = clazz.findClass(name);
		if(foundClassDoc != null) {
			try {
				// Should return null if not found
				return repository.findClass(foundClassDoc.qualifiedName());
			} catch(FrankDocException e) {
				log.error("Error searching for class [{}]", foundClassDoc.qualifiedName());
			}
		}
		return null;
	}
}
