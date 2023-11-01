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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

class TransitiveImplementedInterfaceBrowser<T> {
	final List<FrankClass> interfaces = new ArrayList<>();
	final Set<String> interfaceNamesDone = new HashSet<>();

	TransitiveImplementedInterfaceBrowser(FrankClass clazz) {
		uniquelyEnqueueSuperInterfaces(clazz);
	}

	private void uniquelyEnqueueSuperInterfaces(FrankClass clazz) {
		uniquelyEnqueueInterfacesOf(clazz);
		if(! interfaces.isEmpty()) {
			for(int index = 0; index < interfaces.size(); ++index) {
				uniquelyEnqueueInterfacesOf(interfaces.get(index));
			}
		}
	}

	private void uniquelyEnqueueInterfacesOf(FrankClass clazz) {
		Arrays.asList(clazz.getInterfaces()).forEach(this::enqueueUniquely);
	}

	private void enqueueUniquely(FrankClass clazz) {
		if (!interfaceNamesDone.contains(clazz.getName())) {
			interfaceNamesDone.add(clazz.getName());
			interfaces.add(clazz);
		}
	}

	List<FrankClass> getInterfacesAndTheirAncestors() {
		return interfaces;
	}

	T search(Function<FrankClass, T> testFunction) {
		for (FrankClass intf : interfaces) {
			T result = testFunction.apply(intf);
			if (result != null) {
				return result;
			}
		}
		return null;
	}
}
