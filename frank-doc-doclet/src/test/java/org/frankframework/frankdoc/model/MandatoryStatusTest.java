package org.frankframework.frankdoc.model;

import org.frankframework.frankdoc.feature.Mandatory;
import org.frankframework.frankdoc.feature.Optional;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MandatoryStatusTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.mandatory.status.";

	@Parameters(name = "Test method {0} has status {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{"setNotMandatory", MandatoryStatus.OPTIONAL},
			{"setMandatoryIgnoreCompatibilityByAnnotation" , MandatoryStatus.BECOMES_MANDATORY},
			{"setMandatoryIgnoreCompatibilityByTag", MandatoryStatus.BECOMES_MANDATORY},
			{"setSimplyMandatoryByAnnotation", MandatoryStatus.MANDATORY},
			{"setSimplyMandatoryByTag", MandatoryStatus.MANDATORY},
			{"setOptional", MandatoryStatus.OPTIONAL},
		});
	}

	@Parameter(0)
	public String methodName;

	@Parameter(1)
	public MandatoryStatus expectedMandatoryStatus;

	@Test
	public void test() throws Exception {
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		FrankClass clazz = repository.findClass(PACKAGE + "Subject");
		FrankMethod testMethod = Arrays.asList(clazz.getDeclaredMethods()).stream()
				.filter(m -> m.getName().equals(methodName))
				.collect(Collectors.toList()).get(0);
		MandatoryStatus actualMandatoryStatus = null;
		actualMandatoryStatus = MandatoryStatus.of(Mandatory.getInstance().valueOf(testMethod), Optional.getInstance().isSetOn(testMethod));
		assertEquals(expectedMandatoryStatus, actualMandatoryStatus);
	}
}
