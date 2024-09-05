/*
Copyright 2023 WeAreFrank!

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
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankDocException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class ExcludeFromTypeFeature {
	private static Logger log = LogUtil.getLogger(ExcludeFromTypeFeature.class);
	private static final String ANNOTATION_NAME = "org.frankframework.doc.ExcludeFromType";
	private static final String TAG_NAME = "@ff.excludeFromType";

	// In practice, this map will have only one member because we work with only one class repository.
	// Conceptually, the result of calling excludeFrom depends both on the test class and on the repository.
	// That becomes more clear by wrapping the applicable FrankClassRepository in the instance.
	private static final Map<FrankClassRepository, ExcludeFromTypeFeature> INSTANCES = new HashMap<>();

	private final FrankClassRepository repository;

	// We cache results of method excludeFrom to ensure that each result is calculated only once.
	// If errors or warnings occur, they are not duplicated in the log.
	private Map<FrankClass, Set<FrankClass>> checkedClasses = new HashMap<>();

	private ExcludeFromTypeFeature(FrankClassRepository repository) {
		this.repository = repository;
	}

	public static final ExcludeFromTypeFeature getInstance(FrankClassRepository repository) {
		if(! INSTANCES.containsKey(repository)) {
			INSTANCES.put(repository, new ExcludeFromTypeFeature(repository));
		}
		return INSTANCES.get(repository);
	}

	public Set<FrankClass> excludedFrom(FrankClass testClass) {
		if(checkedClasses.containsKey(testClass)) {
			return new HashSet<>(checkedClasses.get(testClass));
		}
		String[] classNames = getReferredClassNames(testClass);
		if(classNames != null) {
			return checkClassNamesAndGetClasses(classNames, testClass);
		} else {
			return null;
		}
	}

	private String[] getReferredClassNames(FrankClass testClass) {
		String[] classNames = null;
		FrankAnnotation annotation = testClass.getAnnotation(ANNOTATION_NAME);
		if(annotation != null) {
			classNames = (String[]) annotation.getValue();
		} else {
			String tagValue = testClass.getJavaDocTag(TAG_NAME);
			if(tagValue != null) {
				classNames = Arrays.asList(tagValue.split(",")).stream()
						.map(String::trim)
						.collect(Collectors.toList()).toArray(new String[] {});
			}
		}
		return classNames;
	}

	private Set<FrankClass> checkClassNamesAndGetClasses(String[] classNames, FrankClass testClass) {
		Set<FrankClass> result = new HashSet<>();
		for(String className: classNames) {
			try {
				FrankClass clazz = repository.findClass(className);
				if(clazz == null) {
					log.error("Feature ExcludeFromTypeFeature of class [{}] refers to class [{}], but that is not a class", testClass.getName(), className);
				} else {
					result.add(clazz);
					if(! testClass.extendsOrImplements(clazz)) {
						log.warn("Feature ExcludeFromTypeFeature of [{}] mentions [{}] unnecessarily, because there is no ancestor relation", testClass.getName(), clazz.getName());
					}
				}
			} catch(FrankDocException e) {
				log.error("Feature ExcludeFromTypeFeature of class [{}] refers to class [{}]; caught exception while checking whether that is a class", testClass.getName(), className, e);
			}
		}
		checkedClasses.put(testClass, result);
		return result;
	}
}
