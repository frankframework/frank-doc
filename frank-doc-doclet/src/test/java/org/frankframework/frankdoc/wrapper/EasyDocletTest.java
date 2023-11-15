package org.frankframework.frankdoc.wrapper;

import org.junit.Test;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

public class EasyDocletTest {

	@Test
	public void test() throws Exception {
		for (Element element : TestUtil.getTypeElements(null, "org.frankframework.frankdoc.testtarget.doclet")) {
			if (element instanceof TypeElement) {
				System.out.println(((TypeElement) element).getQualifiedName().toString());
			}
		}
	}
}
