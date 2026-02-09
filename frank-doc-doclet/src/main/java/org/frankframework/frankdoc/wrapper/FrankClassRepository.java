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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.sun.source.util.DocTrees;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class FrankClassRepository {
	private final @Getter(AccessLevel.PACKAGE) Set<String> excludeFiltersForSuperclass;

	private final Map<String, FrankClass> classesByName = new LinkedHashMap<>();
	private final Map<String, FrankNonCompiledClassDoclet> nonFrankClassesByName = new HashMap<>();
	private final Set<FrankClass> filteredClassesForInterfaceImplementations;
	/**
	 * Set to {@literal true} if a Java Class by the name {@literal Include} was present in the loaded Frank!Framework classes.
	 * This will be true in all production but not in all unit test situations.
	 * If {@literal true} then generate additional root elements and add the {@literal Include} type to the XSD for all types that
	 * can be extended by includes.
	 */
	private final @Getter boolean includeTypePresent;

	public FrankClassRepository(DocTrees docTrees, Set<? extends Element> classElements, Set<String> includeFilters, Set<String> excludeFilters, Set<String> excludeFiltersForSuperclass) {
		this.excludeFiltersForSuperclass = new HashSet<>(excludeFiltersForSuperclass);

		// This populates the map classesByName
		classElements.stream()
			.filter(TypeElement.class::isInstance)
			.map(TypeElement.class::cast)
			.forEach(typeElement -> findOrCreateClass(typeElement, docTrees));

		includeTypePresent = classesByName
			.keySet()
			.stream()
			.anyMatch(name -> name.endsWith("Include"));

		final Set<String> correctedIncludeFilters = includeFilters.stream()
			.map(FrankClassRepository::removeTrailingDot)
			.collect(Collectors.toSet());
		filteredClassesForInterfaceImplementations = classesByName.values().stream()
			.filter(c -> correctedIncludeFilters.stream().anyMatch(i -> c.getPackageName().startsWith(i)))
			.filter(c -> !excludeFilters.contains(c.getName()))
			.filter(FrankClass::isTopLevel) // Filter to get the same as with reflection (no inner classes e.g.)
			.collect(Collectors.toSet());
		for (FrankClass c : filteredClassesForInterfaceImplementations) {
			setInterfaceImplementations(c);
		}
		classesByName.values().forEach(FrankClass::addMultiplyInheritedMethodPlaceholders);
	}

	private FrankClass findOrCreateClass(TypeElement typeElement, DocTrees docTrees) {
		if (classesByName.containsKey(typeElement.getQualifiedName().toString())) {
			return classesByName.get(typeElement.getQualifiedName().toString());
		}
		FrankClass result = new FrankClass(typeElement, docTrees, this);
		classesByName.put(result.getName(), result);

		TypeElement superTypeElement = FrankDocletUtils.getSuperclassElement(typeElement);
		if (superTypeElement != null) {
			FrankClass superClazz = findOrCreateClass(superTypeElement, docTrees);
			superClazz.addChild(result.getName());
		}
		return result;
	}

	private void setInterfaceImplementations(FrankClass clazz) {
		List<FrankClass> implementedInterfaces = clazz.getInterfacesAsList().stream()
			.distinct()
			.toList();
		if (!implementedInterfaces.isEmpty()) {
			log.trace("Directly implemented interfaces for class [{}]: [{}]", clazz::getSimpleName, () -> implementedInterfaces.stream().map(FrankClass::getSimpleName).collect(Collectors.joining(", ")));
		}
		try {
			for (FrankClass interfaze : implementedInterfaces) {
				interfaze.recursivelyAddInterfaceImplementation(clazz);
				new TransitiveImplementedInterfaceBrowser<FrankClass>(interfaze).search(i -> loggedAddInterfaceImplementation(i, clazz));
			}
		} catch (FrankDocException e) {
			log.error("Error setting implemented interfaces of class {}", clazz.getName(), e);
		}
	}

	private FrankClass loggedAddInterfaceImplementation(FrankClass interfaze, FrankClass clazz) {
		log.trace("Considering ancestor interface {}", interfaze::getName);
		try {
			interfaze.recursivelyAddInterfaceImplementation(clazz);
		} catch (FrankDocException e) {
			log.error("Could not recurse over children of {} to set them as implementations of {}", clazz.getName(), interfaze.getName(), e);
		}
		return null;
	}

	boolean classIsAllowedAsInterfaceImplementation(FrankClass clazz) {
		return filteredClassesForInterfaceImplementations.contains(clazz);
	}

	public FrankClass findClass(String fullName) throws FrankDocException {
		try {
			return classesByName.get(fullName);
		} catch (Exception e) {
			throw new FrankDocException(e.getMessage(), e.getCause());
		}
	}

	public List<FrankClass> findDescendents(FrankClass parent) {
		return this.getAllClasses().stream()
			.filter(cls -> !cls.isAbstract() && !cls.isInterface() && cls.extendsOrImplements(parent))
			.toList();
	}

	public FrankClass findMatchingClass(String simpleClassName) {
		String fullName = classesByName.keySet().stream().filter(c -> c.endsWith(simpleClassName)).findFirst().orElse(null);
		if (fullName != null) {
			return classesByName.get(fullName);
		}
		return null;
	}

	public FrankNonCompiledClassDoclet findOrCreateNonCompiledClass(final String fullName) {
		return nonFrankClassesByName.computeIfAbsent(fullName, FrankNonCompiledClassDoclet::new);
	}

	public List<FrankClass> getAllClasses() {
		return new ArrayList<>(this.classesByName.values());
	}

	/**
	 * Removes a trailing dot from a package name. It is handy to refer to class names like
	 * <code>PACKAGE + simpleName</code> but for this to work the variable <code>PACKAGE</code>
	 * should end with a dot. This function removes that dot to arrive at a plain package name
	 * again.
	 */
	static String removeTrailingDot(String s) {
		if (s.endsWith(".")) {
			return s.substring(0, s.length() - 1);
		} else {
			return s;
		}
	}

	public int size() {
		return classesByName.size();
	}
}
