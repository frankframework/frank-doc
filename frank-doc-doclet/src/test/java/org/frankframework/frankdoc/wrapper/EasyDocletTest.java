package org.frankframework.frankdoc.wrapper;

import org.junit.jupiter.api.Test;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EasyDocletTest {

	@Test
	public void test() throws Exception {
		Set<? extends Element> typeElements = TestUtil.getIncludedElements("org.frankframework.frankdoc.testtarget.doclet");
		assertEquals(50, typeElements.size());

		for (Element element : typeElements) {
			if (element instanceof TypeElement) {
				System.out.println(((TypeElement) element).getQualifiedName().toString());
			}
		}
	}
}
