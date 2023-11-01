package org.frankframework.frankdoc.feature;

import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Parameterized.class)
public class DefaultAndDescriptionTest {
	@Parameters(name = "{0}-{1}-{2}")
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
			{"withReferencedInterfaceValue", "Description with replyAddressFieldsDefault", "replyAddressFieldsDefault"}
		});
	}

	@Parameter(0)
	public String methodToTest;

	@Parameter(1)
	public String expectedDescription;

	@Parameter(2)
	public String expectedDefaultValue;

	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.featurepackage.";
	private static final String CLASS_NAME = PACKAGE + "Documented";

	private FrankMethod method;

	@Before
	public void setUp() throws Exception {
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE, "org.frankframework.frankdoc.testtarget.wrapper.variables");
		FrankClass clazz = repository.findClass(CLASS_NAME);
		method = Arrays.stream(clazz.getDeclaredMethods())
				.filter(m -> m.getName().equals(methodToTest))
				.findFirst()
				.get();
	}

	@Test
	public void test() throws Exception {
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
}
