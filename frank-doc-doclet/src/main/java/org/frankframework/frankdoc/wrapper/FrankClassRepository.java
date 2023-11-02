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

import com.sun.source.util.DocTrees;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FrankClassRepository {
	private static final Logger log = LogUtil.getLogger(FrankClassRepository.class);

	private final @Getter(AccessLevel.PACKAGE) Set<String> excludeFiltersForSuperclass;

	private final Map<String, FrankClass> classesByName = new HashMap<>();
	private final Set<FrankClass> filteredClassesForInterfaceImplementations;

	public FrankClassRepository(DocTrees docTrees, Set<? extends Element> classElements, Set<String> includeFilters, Set<String> excludeFilters, Set<String> excludeFiltersForSuperclass) {
		this.excludeFiltersForSuperclass = new HashSet<>(excludeFiltersForSuperclass);
		classElements.stream()
			.filter(TypeElement.class::isInstance)
			.map(TypeElement.class::cast)
			.forEach(typeElement -> findOrCreateClass(typeElement, docTrees));

		final Set<String> correctedIncludeFilters = includeFilters.stream().map(FrankClassRepository::removeTrailingDot).collect(Collectors.toSet());
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
			.collect(Collectors.toList());
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

	public FrankClass findMatchingClass(String simpleClassName) {
		String fullName = classesByName.keySet().stream().filter(c -> c.endsWith(simpleClassName)).findFirst().orElse(null);
		if (fullName != null) {
			return classesByName.get(fullName);
		}
		return null;
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
}
