package org.frankframework.frankdoc.model;

import java.util.EnumSet;
import java.util.Set;

import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.wrapper.FrankMethod;

public enum MandatoryStatus {
	/**
	 * Attribute or config child is mandatory. It should also be mandatory when a config is validated.
	 */
	MANDATORY,

	/**
	 * Attribute or config child is mandatory, but only in the text editor of Frank developers.
	 * For backward compatibility, the Frank!Framework should accept configs without the
	 * config child or attribute.
	 */
	BECOMES_MANDATORY,

	/**
	 * Attribute or config child is optional.
	 */
	OPTIONAL;

	static MandatoryStatus fromMethod(FrankMethod method) throws FrankDocException {
		Set<Feature> features = EnumSet.noneOf(Feature.class);
		// We cannot implement this with streams because isEffectivelySetOn throws an exception.
		for(Feature f: EnumSet.of(Feature.MANDATORY, Feature.BECOMES_MANDATORY, Feature.OPTIONAL)) {
			if(f.isEffectivelySetOn(method)) {
				features.add(f);
			}
		}
		return fromFeatures(features);
	}

	static MandatoryStatus fromFeatures(Set<Feature> features) {
		if(features.isEmpty()) {
			return MandatoryStatus.OPTIONAL;
		}
		if(features.contains(Feature.OPTIONAL)) {
			return MandatoryStatus.OPTIONAL;
		}
		if(features.contains(Feature.BECOMES_MANDATORY)) {
			return MandatoryStatus.BECOMES_MANDATORY;
		}
		return MandatoryStatus.MANDATORY;
	}
}
