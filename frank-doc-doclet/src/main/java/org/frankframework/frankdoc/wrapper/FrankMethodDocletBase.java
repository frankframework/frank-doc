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

package org.frankframework.frankdoc.wrapper;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.util.LogUtil;

import com.sun.javadoc.MethodDoc;

abstract class FrankMethodDocletBase implements FrankMethod {
	private static Logger log = LogUtil.getLogger(FrankMethodDocletBase.class);

	private final FrankClassDoclet declaringClass;
	private final Set<MethodDoc> warnedMethodsNotInJavaDoc = new HashSet<>();

	FrankMethodDocletBase(FrankClassDoclet declaringClass) {
		this.declaringClass = declaringClass;
	}

	@Override
	public FrankClass getDeclaringClass() {
		return declaringClass;
	}

	@Override
	public FrankAnnotation getAnnotationIncludingInherited(String name) throws FrankDocException {
		Function<FrankMethodDocletBase, FrankAnnotation> getter = m -> m.getAnnotation(name);
		return searchIncludingInherited(getter);
	}

	@Override
	public String getJavaDocIncludingInherited() throws FrankDocException {
		Function<FrankMethodDocletBase, String> getter = m -> m.getJavaDoc();
		return searchIncludingInherited(getter);
	}

	@Override
	public String getJavaDocTagIncludingInherited(String tagName) throws FrankDocException {
		Function<FrankMethodDocletBase, String> getter = m -> m.getJavaDocTag(tagName);
		return searchIncludingInherited(getter);		
	}

	private <T> T searchIncludingInherited(Function<FrankMethodDocletBase, T> getter) throws FrankDocException {
		T result = searchExcludingImplementedInterfaces(getter);
		if(result == null) {
			result = searchImplementedInterfaces(this.getDeclaringClass(), this.getSignature(), getter);
		}
		return result;
	}

	private <T> T searchExcludingImplementedInterfaces(Function<FrankMethodDocletBase, T> getter) throws FrankDocException {
		T result = getter.apply(this);
		if(result != null) {
			return result;
		}
		MethodDoc overriddenMethodDoc = getOverriddenMethodDoc();
		if(overriddenMethodDoc != null) {
			FrankMethodDocletBase overriddenMethod = (FrankMethodDocletBase) declaringClass.recursivelyFindFrankMethod(overriddenMethodDoc);
			if(overriddenMethod != null) {
				return overriddenMethod.searchExcludingImplementedInterfaces(getter);
			} else {
				if(! warnedMethodsNotInJavaDoc.contains(overriddenMethodDoc)) {
					warnedMethodsNotInJavaDoc.add(overriddenMethodDoc);
					// To see this warning, run test FrankMethodOverrideTest.whenPackagePrivateOverriddenByPublicThenOnlyChildMethodConsidered().
					log.warn("Class {} inherits method {}, but no annotations or JavaDocs are known because the overridden method is not public.",
							() -> declaringClass.getName(), () -> overriddenMethodDoc.qualifiedName());
				}
			}
		}
		return null;
	}

	abstract MethodDoc getOverriddenMethodDoc();

	private <T> T searchImplementedInterfaces(FrankClass clazz, String methodSignature, Function<FrankMethodDocletBase, T> getter) throws FrankDocException {
		TransitiveImplementedInterfaceBrowser<T> interfaceBrowser = new TransitiveImplementedInterfaceBrowser<>((FrankClassDoclet) clazz);
		Function<FrankClass, T> classGetter = interfaze -> ((FrankClassDoclet) interfaze).getMethodItemFromSignature(methodSignature, getter);
		T result = interfaceBrowser.search(classGetter);
		if(result != null) {
			return result;
		}
		if(clazz.getSuperclass() == null) {
			return null;
		}
		return searchImplementedInterfaces(clazz.getSuperclass(), methodSignature, getter);
	}
}
