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

package org.frankframework.frankdoc.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankMethod;

/**
 * Class XmlValidator inherits method setName(). The method is overridden both
 * from parent class AbstractPipe and from interface IValidator. In AbstractPipe,
 * the method is mandatory while it is declared optional in IValidator. Although
 * the method is not declared in XmlValidator, the attribute name must be redeclared
 * for XmlValidator to make it optional.
 * 
 * To make this possible, the model should know re-inherited methods. These are
 * non-declared inherited methods that are also overridden from an implemented
 * interface. This class finds the re-inherited methods of a class.
 *
 * @author martijn
 *
 */
class ReInheritedMethods {
	public static FrankMethod[] declaredAndReinheritedFor(FrankClass clazz) {
		List<FrankMethod> result = new ArrayList<>();
		result.addAll(Arrays.asList(clazz.getDeclaredMethods()));
		result.addAll(findFor(clazz));
		return result.toArray(new FrankMethod[] {});
	}

	public static List<FrankMethod> findFor(FrankClass clazz) {
		Map<String, FrankClass> interfazes = new LinkedHashMap<>();
		getInterfaceHierarchy(clazz, interfazes);
		if(interfazes.isEmpty()) {
			return new ArrayList<>();
		} else {
			return getReinheritedMethods(clazz, interfazes);
		}
	}

	private static List<FrankMethod> getReinheritedMethods(FrankClass clazz, Map<String, FrankClass> interfazes) {
		List<FrankMethod> result = new ArrayList<>();
		Set<String> declaredMethodSignatures = Arrays.asList(clazz.getDeclaredMethods()).stream()
				.map(FrankMethod::getSignature)
				.collect(Collectors.toSet());
		Set<String> methodSignaturesFromImplementedInterfaces = interfazes.values().stream()
				.flatMap(ReInheritedMethods::methodSignaturesOf)
				.collect(Collectors.toSet());
		for(FrankMethod candidate: clazz.getDeclaredAndInheritedMethods()) {
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

	private static void getInterfaceHierarchy(FrankClass clazz, Map<String, FrankClass> result) {
		List<FrankClass> interfazes = Arrays.asList(clazz.getInterfaces());
		for(FrankClass currentInterface: interfazes) {
			if(! result.containsKey(currentInterface.getName())) {
				result.put(currentInterface.getName(), currentInterface);
				getInterfaceHierarchy(currentInterface, result);
			}
		}
	}
}
