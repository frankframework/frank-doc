package org.frankframework.frankdoc.feature;

import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DefaultAndDescriptionTest {
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{"notDocumented", null, null},
			{"withDefaultAnnotation", null, "default value"},
			{"withDefaultTag", null, "default value"},
			{"withJavaDoc", "My description", null},
			{"withFullIbisDoc", "My description", "default value"},
			{"withIbisDocNoOrder", "My description", "default value"},
			{"withIbisDocNoDefault", "My description", null},
			{"withIbisDocNoDefaultNoOrder", "My description", null},
			{"withReferencedValue", "Description with my value", "my value"},
			{"withReferencedInterfaceValue", "Description with replyAddressFieldsDefault", "replyAddressFieldsDefault"},
			{"setTransacted", "controls the use of transactions", null}
		});
	}
	public String methodToTest;
	public String expectedDescription;
	public String expectedDefaultValue;

	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.featurepackage.";
	private static final String CLASS_NAME = PACKAGE + "Documented";

	private FrankMethod method;

	@BeforeEach
	public void setUp() throws Exception {
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE, "org.frankframework.frankdoc.testtarget.wrapper.variables");
		FrankClass clazz = repository.findClass(CLASS_NAME);
		method = Arrays.stream(clazz.getDeclaredMethods())
				.filter(m -> m.getName().equals(methodToTest))
				.findFirst()
				.get();
	}

	@MethodSource("data")
	@ParameterizedTest(name = "{0}-{1}-{2}")
	public void test(String methodToTest, String expectedDescription, String expectedDefaultValue) throws Exception {
		initDefaultAndDescriptionTest(methodToTest, expectedDescription, expectedDefaultValue);
		String description = Description.getInstance().valueOf(method);
		String defaultValue = Default.getInstance().valueOf(method);
		if(expectedDescription == null) {
			assertNull(description);
		} else {
			assertEquals(expectedDescription, description);
		}
		if(expectedDefaultValue == null) {
			assertNull(defaultValue);
		} else {
			assertEquals(expectedDefaultValue, defaultValue);
		}
	}

	public void initDefaultAndDescriptionTest(String methodToTest, String expectedDescription, String expectedDefaultValue) {
		this.methodToTest = methodToTest;
		this.expectedDescription = expectedDescription;
		this.expectedDefaultValue = expectedDefaultValue;
	}
}
