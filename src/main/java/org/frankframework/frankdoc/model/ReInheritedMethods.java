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

class ReInheritedMethods {
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
