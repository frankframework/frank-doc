package org.frankframework.frankdoc.model;

import org.frankframework.frankdoc.wrapper.FrankClass;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankMethod;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class AncestorMethodBrowserTest {
	private static final String PACKAGE = "org.frankframework.frankdoc.testtarget.browse.ancestor.methods.";
	private static final String CLASS_NAME = PACKAGE + "Child";
	private FrankClass clazz;
	private AncestorMethodBrowser instanceWithRef;
	private AncestorMethodBrowser instanceNoRef;
	private List<String> ancestorMethodClasses = new ArrayList<>();

	@BeforeEach
	public void setUp() throws Exception {
		FrankClassRepository repository = TestUtil.getFrankClassRepositoryDoclet(PACKAGE);
		instanceWithRef = new AncestorMethodBrowser(repository, AncestorMethodBrowser.References.WITH_REFERENCES);
		instanceNoRef = new AncestorMethodBrowser(repository, AncestorMethodBrowser.References.WITHOUT_REFERENCES);
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
		instanceWithRef.browse(findMethod("setAttributeWithReference"), this::keepClassOfMethod);
		assertArrayEquals(new String[] {"Child", "ReferenceTarget", "ReferenceTargetParent"}, ancestorMethodClasses.toArray(new String[] {}));
	}

	@Test
	public void whenNoReferenceThenClassHierarchyBrowsed_refsChecked() {
		instanceWithRef.browse(findMethod("setAttributeNoReference"), this::keepClassOfMethod);
		assertArrayEquals(new String[] {"Child", "Parent", "GrandParent"}, ancestorMethodClasses.toArray(new String[] {}));
	}

	@Test
	public void whenNoReferenceThenClassHierarchyBrowsed_refsNotChecked() {
		instanceNoRef.browse(findMethod("setAttributeNoReference"), this::keepClassOfMethod);
		assertArrayEquals(new String[] {"Child", "Parent", "GrandParent"}, ancestorMethodClasses.toArray(new String[] {}));
	}

	@Test
	public void whenReferencesNotCheckedThenHierarchyBrowsed() {
		instanceNoRef.browse(findMethod("setAttributeWithReference"), this::keepClassOfMethod);
		assertArrayEquals(new String[] {"Child", "Parent"}, ancestorMethodClasses.toArray(new String[] {}));
	}
}
