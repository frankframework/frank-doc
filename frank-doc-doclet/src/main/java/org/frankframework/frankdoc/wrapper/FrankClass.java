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

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.util.DocTrees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.doclet.DocletHelper;
import org.frankframework.frankdoc.util.LogUtil;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FrankClass implements FrankType {
	private static final Logger log = LogUtil.getLogger(FrankClass.class);

	private final FrankClassRepository repository;
	private final TypeElement clazz;

	private final DocCommentTree docCommentTree;
	private final Set<String> childClassNames = new HashSet<>();
	private final Map<String, FrankClass> interfaceImplementationsByName = new HashMap<>();
	private final LinkedHashMap<ExecutableElement, FrankMethod> frankMethodsByDocletMethod = new LinkedHashMap<>();
	private final Map<String, FrankMethodDoclet> methodsBySignature = new HashMap<>();
	private final Map<String, FrankAnnotation> frankAnnotationsByName;
	private final @Getter(AccessLevel.PACKAGE) List<MultiplyInheritedMethodPlaceholder> multiplyInheritedMethodPlaceholders = new ArrayList<>();
	private final Map<String, String> fields = new HashMap<>();
	private final Map<String, FrankEnumConstant> enumFields = new LinkedHashMap<>();

	FrankClass(TypeElement element, DocTrees docTrees, FrankClassRepository repository) {
		log.trace("Creating FrankClass for [{}]", element.getQualifiedName());
		this.repository = repository;
		this.clazz = element;
		if (docTrees != null) {
			this.docCommentTree = docTrees.getDocCommentTree(element);
		} else {
			this.docCommentTree = null;
		}

		// Add class attributes and methods
		for (Element e : element.getEnclosedElements()) {
			processElement(docTrees, e);
		}

		if (log.isTraceEnabled() && !frankMethodsByDocletMethod.isEmpty()) {
			log.trace("Class [{}] has the following methods:", element.getQualifiedName());
			frankMethodsByDocletMethod.values().forEach(m -> log.trace(" Method [{}], public: [{}]", m.getName(), m.isPublic()));
		}

		AnnotationMirror[] annotationMirrors = element.getAnnotationMirrors().toArray(new AnnotationMirror[]{});
		frankAnnotationsByName = FrankDocletUtils.getFrankAnnotationsByName(annotationMirrors);
	}

	protected void processElement(DocTrees docTrees, Element e) {
		ElementKind kind = e.getKind();
		if (kind == ElementKind.METHOD) {
			ExecutableElement executableElement = (ExecutableElement) e;
			FrankMethodDoclet frankMethodDoclet = new FrankMethodDoclet(executableElement, this, docTrees.getDocCommentTree(executableElement));
			if (!frankMethodDoclet.isPublic()) // Skip non-public methods (private/package privates)
				return;
			frankMethodsByDocletMethod.put(executableElement, frankMethodDoclet);
			methodsBySignature.put(frankMethodDoclet.getSignature(), frankMethodDoclet);
		} else if (kind == ElementKind.ENUM_CONSTANT) {
			VariableElement variableElement = (VariableElement) e;
			enumFields.put(variableElement.getSimpleName().toString(), new FrankEnumConstantDoclet(variableElement, docTrees.getDocCommentTree(variableElement)));
		} else if (kind == ElementKind.FIELD) {
			VariableElement variableElement = (VariableElement) e;
			fields.put(variableElement.getSimpleName().toString(), variableElement.getConstantValue() != null ? variableElement.getConstantValue().toString() : null);
		}
	}

	void addChild(String className) {
		childClassNames.add(className);
	}

	void recursivelyAddInterfaceImplementation(FrankClass implementation) throws FrankDocException {
		if (repository.classIsAllowedAsInterfaceImplementation(implementation)) {
			log.trace("Interface {} is implemented by {}", this::getName, implementation::getName);
			// TODO: Test that children of omitted classes can be accepted again.
			interfaceImplementationsByName.put(implementation.getName(), implementation);
		} else {
			log.trace("From interface {} omitted implementation because of filtering {}", this::getName, implementation::getName);
		}
		for (String implementationChildClassName : implementation.childClassNames) {
			FrankClass implementationChild = repository.findClass(implementationChildClassName);
			recursivelyAddInterfaceImplementation(implementationChild);
		}
	}

	public boolean isEnum() {
		return clazz.getKind().equals(ElementKind.ENUM);
	}

	public String getName() {
		return clazz.getQualifiedName().toString();
	}

	public FrankAnnotation[] getAnnotations() {
		List<FrankAnnotation> values = new ArrayList<>(frankAnnotationsByName.values());
		return values.toArray(new FrankAnnotation[]{});
	}

	public FrankAnnotation getAnnotation(String name) {
		return frankAnnotationsByName.get(name);
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	public String getSimpleName() {
		String result = clazz.getSimpleName().toString();
		// For inner classes, we now have result == <outerClassName>.<innerClassName>
		// We want only <innerClassName>
		if (result.contains(".")) {
			result = result.substring(result.lastIndexOf(".") + 1);
		}
		return result;
	}

	public String getPackageName() {
		return clazz.getEnclosingElement().toString();
	}

	public FrankClass getSuperclass() {
		FrankClass result = null;
		TypeElement superClazz = FrankDocletUtils.getSuperclassElement(clazz);
		if (superClazz == null) {
			return null;
		}
		try {
			String superclassQualifiedName = superClazz.getQualifiedName().toString();
			boolean omit = repository.getExcludeFiltersForSuperclass().stream()
				.anyMatch(superclassQualifiedName::startsWith);
			if (omit) {
				return null;
			}
			result = repository.findClass(superclassQualifiedName);
		} catch (FrankDocException e) {
			log.error("Could not get superclass of {}", getName(), e);
		}
		return result;
	}

	/**
	 * Get super interfaces of an interface, or interfaces implemented by a class.
	 */
	public FrankClass[] getInterfaces() {
		List<FrankClass> resultList = getInterfacesAsList();
		return resultList.toArray(new FrankClass[]{});
	}

	List<FrankClass> getInterfacesAsList() {
		List<FrankClass> resultList = new ArrayList<>();
		for (TypeMirror interfaceDoc : clazz.getInterfaces()) {
			try {
				// Need to retrieve this full name, otherwise class name includes type parameters, e.g. org.ClassName<String>
				String fullNameWithoutTypeInfo = ((Type.ClassType) interfaceDoc).tsym.toString();
				FrankClass interfaze = repository.findClass(fullNameWithoutTypeInfo);
				if (interfaze != null) {
					resultList.add(interfaze);
				}
			} catch (FrankDocException e) {
				log.error("Error searching for {}", interfaceDoc, e);
			}
		}
		return resultList;
	}


	public boolean isAbstract() {
		return clazz.getModifiers().stream().anyMatch(m -> m == Modifier.ABSTRACT);
	}


	public boolean isInterface() {
		return clazz.getKind().isInterface();
	}


	public boolean isPublic() {
		return clazz.getModifiers().stream().anyMatch(m -> m == Modifier.PUBLIC);
	}

	/**
	 * Assumes that this object models a Java interface and get the non-abstract interface implementations.
	 */
	public List<FrankClass> getInterfaceImplementations() throws FrankDocException {
		if (!isInterface()) {
			throw new FrankDocException(String.format("Cannot get implementations of non-interface [%s]", getName()), null);
		}
		return interfaceImplementationsByName.values().stream()
			// Remove abstract classes to make it the same as reflection does it.
			.filter(c -> !c.isAbstract())
			.collect(Collectors.toList());
	}


	public FrankMethod[] getDeclaredMethods() {
		List<FrankMethod> resultList = new ArrayList<>(frankMethodsByDocletMethod.values());
		return resultList.toArray(new FrankMethod[]{});
	}


	public FrankMethod[] getDeclaredAndInheritedMethods() {
		List<FrankMethod> resultList = getDeclaredAndInheritedMethodsAsMap().values().stream()
			.filter(FrankMethod::isPublic)
			.collect(Collectors.toList());
		FrankMethod[] result = new FrankMethod[resultList.size()];
		for (int i = 0; i < resultList.size(); ++i) {
			result[i] = resultList.get(i);
		}
		return result;
	}

	private Map<ExecutableElement, FrankMethod> getDeclaredAndInheritedMethodsAsMap() {
		final Map<ExecutableElement, FrankMethod> result = new HashMap<>();
		if (getSuperclass() != null) {
			result.putAll((getSuperclass()).getDeclaredAndInheritedMethodsAsMap());
		}
		List<FrankMethod> declaredMethodList = Arrays.asList(getDeclaredMethods());
		for (FrankMethod declaredMethod : declaredMethodList) {
			((FrankMethodDoclet) declaredMethod).removeOverriddenFrom(result);
		}
		declaredMethodList.forEach(dm -> ((FrankMethodDoclet) dm).addToRepository(result));
		return result;
	}


	public FrankEnumConstant[] getEnumConstants() {
		return enumFields.values().toArray(new FrankEnumConstant[]{});
	}

	FrankClassRepository getRepository() {
		return repository;
	}

	FrankMethod recursivelyFindFrankMethod(ExecutableElement ExecutableElement) {
		if (frankMethodsByDocletMethod.containsKey(ExecutableElement)) {
			return frankMethodsByDocletMethod.get(ExecutableElement);
		} else if (getSuperclass() != null) {
			return (getSuperclass()).recursivelyFindFrankMethod(ExecutableElement);
		} else {
			return null;
		}
	}

	FrankMethodDoclet getMethodFromSignature(String signature) {
		return methodsBySignature.get(signature);
	}

	private FrankMethodDoclet recursivelyGetMethodFromSignature(String signature) {
		FrankMethodDoclet result = getMethodFromSignature(signature);
		if (result == null && getSuperclass() != null) {
			return getSuperclass().recursivelyGetMethodFromSignature(signature);
		}
		return result;
	}

	<T> T getMethodItemFromSignature(String methodSignature, Function<FrankMethodDocletBase, T> getter) {
		FrankMethodDoclet frankMethod = getMethodFromSignature(methodSignature);
		if (frankMethod != null) {
			return getter.apply(frankMethod);
		}
		return null;
	}

	public String getJavaDoc() {
		return docCommentTree == null ? null : DocletHelper.convertDocTreeListToStr(docCommentTree.getFullBody());
	}

	boolean isTopLevel() {
		return !((Symbol.ClassSymbol) clazz).isInner();
	}

	public String toString() {
		return getName();
	}

	public FrankAnnotation getAnnotationIncludingInherited(String annotationFullName) throws FrankDocException {
		Function<FrankClass, FrankAnnotation> getter = c -> c.getAnnotation(annotationFullName);
		return getIncludingInherited(getter).orElse(null);
	}

	private <T> Optional<T> getIncludingInherited(Function<FrankClass, T> getter) throws FrankDocException {
		T result = getExcludingImplementedInterfaces(getter);
		if (result == null) {
			result = getFromImplementedInterfaces(getter);
		}
		return Optional.ofNullable(result);
	}

	private <T> T getExcludingImplementedInterfaces(Function<FrankClass, T> getter) throws FrankDocException {
		T result = getter.apply(this);
		if ((result == null) && (getSuperclass() != null)) {
			result = getSuperclass().getExcludingImplementedInterfaces(getter);
		}
		return result;
	}

	private <T> T getFromImplementedInterfaces(Function<FrankClass, T> getter) throws FrankDocException {
		TransitiveImplementedInterfaceBrowser<T> browser = new TransitiveImplementedInterfaceBrowser<>(this);
		T result = browser.search(getter::apply);
		if ((result == null) && (getSuperclass() != null)) {
			result = getSuperclass().getFromImplementedInterfaces(getter);
		}
		return result;
	}

	public void browseAncestors(Consumer<FrankClass> handler) throws FrankDocException {
		Function<FrankClass, Boolean> getter = c -> adapt(c, handler);
		getIncludingInherited(getter);
	}

	private Boolean adapt(FrankClass c, Consumer<FrankClass> handler) {
		handler.accept(c);
		return null;
	}

	public boolean extendsOrImplements(FrankClass ancestorCandidate) {
		Function<FrankClass, Boolean> getter = c -> c.equals(ancestorCandidate) ? true : null;
		try {
			return getIncludingInherited(getter).orElse(false);
		} catch (FrankDocException e) {
			log.error("Caught exception while checking if class [{}] has ancestor [{}]", this.getName(), ancestorCandidate.getName(), e);
			return false;
		}
	}

	public String getJavaDocTag(String tagName) {
		return FrankDocletUtils.getJavaDocTag(docCommentTree, tagName);
	}

	public List<String> getAllJavaDocTagsOf(String tagName) {
		if (docCommentTree == null || StringUtils.isBlank(tagName)) {
			return Collections.emptyList();
		}
		ArrayList<String> tagValues = new ArrayList<>();
		for (DocTree docTree : docCommentTree.getBlockTags()) {
			String foundTagName = docTree.toString().split(" ")[0];
			if (foundTagName.equals(tagName)) {
				tagValues.add(docTree.toString().substring(foundTagName.length()).trim());
			}
		}
		return tagValues;
	}

	public String getJavaDocTagIncludingInherited(String tagName) throws FrankDocException {
		Function<FrankClass, String> getter = c -> c.getJavaDocTag(tagName);
		return getIncludingInherited(getter).orElse(null);
	}

	public FrankMethod[] getDeclaredMethodsAndMultiplyInheritedPlaceholders() {
		List<FrankMethod> result = new ArrayList<>();
		result.addAll(frankMethodsByDocletMethod.values());
		result.addAll(multiplyInheritedMethodPlaceholders);
		return result.toArray(new FrankMethod[]{});
	}

	void addMultiplyInheritedMethodPlaceholders() {
		MultiplyInheritedMethodBrowser handler = new MultiplyInheritedMethodBrowser();
		TransitiveImplementedInterfaceBrowser<Object> browser = new TransitiveImplementedInterfaceBrowser<>(this);
		browser.search(c -> {
			handler.acceptTransitivelyInheritedInterface(c);
			return null;
		});
	}

	private class MultiplyInheritedMethodBrowser {
		private final Set<String> doNotAddAgain = new HashSet<>();

		MultiplyInheritedMethodBrowser() {
			List<String> declaredMethodSignatures = Arrays.asList(FrankClass.this.getDeclaredMethods()).stream().map(FrankMethod::getSignature).collect(Collectors.toList());
			doNotAddAgain.addAll(declaredMethodSignatures);
		}

		void acceptTransitivelyInheritedInterface(FrankClass interfaze) {
			FrankMethod[] methods = interfaze.getDeclaredMethods();
			for (FrankMethod method : methods) {
				String signature = method.getSignature();
				if (!doNotAddAgain.contains(signature)) {
					multiplyInheritedMethodPlaceholders.add(new MultiplyInheritedMethodPlaceholder(getParentMethod(signature, method), FrankClass.this));
					doNotAddAgain.add(signature);
				}
			}
		}

		private FrankMethodDoclet getParentMethod(String signature, FrankMethod interfaceMethod) {
			// We will not get declared methods here because declared methods have been filtered by the constructor of this inner class.
			FrankMethodDoclet parentMethodFromClass = recursivelyGetMethodFromSignature(signature);
			if (parentMethodFromClass == null) {
				return (FrankMethodDoclet) interfaceMethod;
			} else {
				return parentMethodFromClass;
			}
		}
	}

	public String resolveValue(String variable, Function<FrankEnumConstant, String> enumHandler) {
		Function<FrankClass, String> getter = frankClass -> resolveValueImpl(frankClass, variable, enumHandler);
		try {
			return getIncludingInherited(getter).orElse(null);
		} catch (FrankDocException e) {
			log.error("Error resolving variable [{}]", variable);
			return null;
		}
	}

	private String resolveValueImpl(FrankClass owner, String variable, Function<FrankEnumConstant, String> enumHandler) {
		String value = owner.fields.get(variable);
		if (value != null) {
			return value;
		}
		FrankEnumConstant valueEnumConstant = owner.enumFields.get(variable);
		if (valueEnumConstant != null) {
			return enumHandler.apply(valueEnumConstant);
		}
		return null;
	}

	/**
	 * Find a class in the repository that is enclosed by this class. E.g. classes in the same package
	 * Secondly, search for elements inside this class.
	 *
	 * @param name
	 * @return
	 */
	public FrankClass findClass(String name) {
		TypeElement foundTypeElement = clazz.getEnclosingElement().getEnclosedElements().stream()
			.filter(e -> e.getSimpleName().toString().equals(name))
			.findFirst().map(e -> (TypeElement) e)
			.orElse(null);
		// Search for elements inside this class, such as embedded Enums.
		if (foundTypeElement == null) {
			foundTypeElement = clazz.getEnclosedElements().stream()
				.filter(e -> e.getSimpleName().toString().equals(name))
				.findFirst().map(e -> (TypeElement) e)
				.orElse(null);
		}
		if (foundTypeElement != null) {
			try {
				return repository.findClass(foundTypeElement.getQualifiedName().toString());
			} catch (FrankDocException e) {
				log.error("Error searching for class [{}]", foundTypeElement.getQualifiedName());
			}
		}
		// If still not found, try searching everywhere
		try {
			return repository.findClass(name);
		} catch (FrankDocException ignored) {
		}
		return null;
	}

	public FrankClass findMatchingClass(String simpleClassName) {
		return repository.findMatchingClass(simpleClassName);
	}
}
