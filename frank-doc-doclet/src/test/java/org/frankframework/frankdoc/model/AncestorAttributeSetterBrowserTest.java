package org.frankframework.frankdoc.model;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.Before;
import org.junit.Test;

public class AncestorAttributeSetterBrowserTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.browse.ancestor.methods.";
	private static final String CLASS_NAME = PACKAGE + "Child";
	private FrankClass clazz;
	private AncestorAttributeSetterBrowser instance;
	private List<String> ancestorMethodClasses = new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		instance = new AncestorAttributeSetterBrowser(repository);
		clazz = repository.findClass(CLASS_NAME);
	}

	private FrankMethod findMethod(String methodName) {
		return Arrays.asList(clazz.getDeclaredMethods()).stream()
				.filter(m -> m.getName().equals(methodName))
				.findFirst()
				.get();
	}

	private void keepClassOfMethod(FrankMethod ancestorMethod) {
		ancestorMethodClasses.add(ancestorMethod.getDeclaringClass().getSimpleName());
	}

	@Test
	public void whenReferenceThenReferredMethodIsNextAncestor() {
		instance.browse(findMethod("setAttributeWithReference"), this::keepClassOfMethod);
		assertArrayEquals(new String[] {"Child", "ReferenceTarget", "ReferenceTargetParent"}, ancestorMethodClasses.toArray(new String[] {}));
	}

	@Test
	public void whenNoReferenceThenClassHierarchyBrowsed() {
		instance.browse(findMethod("setAttributeNoReference"), this::keepClassOfMethod);
		assertArrayEquals(new String[] {"Child", "Parent", "GrandParent"}, ancestorMethodClasses.toArray(new String[] {}));
	}
}
