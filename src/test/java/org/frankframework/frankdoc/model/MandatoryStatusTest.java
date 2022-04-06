package org.frankframework.frankdoc.model;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MandatoryStatusTest {
	@Parameters(name = "Features {0} give status {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{EnumSet.noneOf(Feature.class), MandatoryStatus.OPTIONAL},
			{EnumSet.of(Feature.OPTIONAL, Feature.MANDATORY), MandatoryStatus.OPTIONAL},
			{EnumSet.of(Feature.BECOMES_MANDATORY, Feature.MANDATORY), MandatoryStatus.BECOMES_MANDATORY},
			{EnumSet.of(Feature.MANDATORY), MandatoryStatus.MANDATORY}
		});
	}

	@Parameter(0)
	public Set<Feature> features;

	@Parameter(1)
	public MandatoryStatus expectedMandatoryStatus;

	@Test
	public void test() {
		assertEquals(expectedMandatoryStatus, MandatoryStatus.fromFeatures(features));
	}
}
