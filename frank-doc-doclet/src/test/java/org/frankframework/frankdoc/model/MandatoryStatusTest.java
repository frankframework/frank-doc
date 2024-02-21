package org.frankframework.frankdoc.model;

import org.frankframework.frankdoc.feature.Mandatory;
import org.frankframework.frankdoc.feature.Optional;
import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MandatoryStatusTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.mandatory.status.";

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

	@MethodSource("data")
	@ParameterizedTest(name = "Test method {0} has status {1}")
	void test(String methodName, MandatoryStatus expectedMandatoryStatus) throws Exception {
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		FrankClass clazz = repository.findClass(PACKAGE + "Subject");
		FrankMethod testMethod = Arrays.stream(clazz.getDeclaredMethods())
				.filter(m -> m.getName().equals(methodName))
				.collect(Collectors.toList()).get(0);
		MandatoryStatus actualMandatoryStatus = null;
		actualMandatoryStatus = MandatoryStatus.of(Mandatory.getInstance().valueOf(testMethod), Optional.getInstance().isSetOn(testMethod));
		assertEquals(expectedMandatoryStatus, actualMandatoryStatus);
	}

}
