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

import java.util.function.Consumer;

import org.frankframework.frankdoc.feature.Reference;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankMethod;

import lombok.extern.log4j.Log4j2;

/**
 * Browse ancestor methods taking into account feature {@link org.frankframework.frankdoc.feature.Reference}.
 *
 * This class was introduced to fix issue https://github.com/ibissource/frank-doc/issues/140.
 * The same feature can have multiple representations. For example, a default value of an
 * attribute can come from an @IbisDoc annotation and also from a @Default annotation. The
 * same is true if a feature can both be set using a JavaDoc tag and a Java annotation.
 *
 * If browse the annotations one-by-one you can come to wrong conclusions. For example, if
 * you first check whether a description is set by an @IbisDoc annotation somewhere in
 * the inheritance hierarchy and then check the @Default annotation the same way, either
 * the @Default or the @IbisDoc takes precedence. This would disregard the place in the
 * inheritance hierarchy.
 *
 * The right way to resolve attribute features is to have an outer loop that iterates
 * over the setter and its ancestor methods. On each level, all annotations should be
 * considered that influence a feature. The iteration must take into account the
 * {@link org.frankframework.frankdoc.feature.Reference} feature. If an attribute
 * setter has @ff.ref, the next ancestor method is the referred method.
 *
 * @author M66C303
 *
 */
@Log4j2
class AncestorMethodBrowser {
	enum References {
		WITH_REFERENCES,
		WITHOUT_REFERENCES;
	}
	private final Reference referenceFeature;
	private final References referenceHandling;

	AncestorMethodBrowser(FrankClassRepository repository, References referenceHandling) {
		referenceFeature = new Reference(repository);
		this.referenceHandling = referenceHandling;
	}

	void browse(FrankMethod attributeSetter, Consumer<FrankMethod> handler) {
		try {
			attributeSetter.browseAncestorsUntilTrue(m -> browseUntilReferenceAndThenBrowseReference(m, handler));
		} catch(FrankDocException e) {
			log.error("Error browsing ancestor methods of [{}]", attributeSetter, e);
		}
	}

	private boolean browseUntilReferenceAndThenBrowseReference(FrankMethod ancestorMethod, Consumer<FrankMethod> handler) {
		handler.accept(ancestorMethod);
		FrankMethod referenced = referenceFeature.valueOf(ancestorMethod);
		if(referenced == null) {
			return false;
		} else {
			if(referenceHandling.equals(References.WITHOUT_REFERENCES)) {
				log.error("No reference allowed on method [{}]", ancestorMethod);
				return false;
			}
			browse(referenced, handler);
			return true;
		}
	}
}
