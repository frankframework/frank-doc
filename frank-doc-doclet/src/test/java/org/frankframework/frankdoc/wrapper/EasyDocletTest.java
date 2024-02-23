package org.frankframework.frankdoc.wrapper;

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.model.FrankElement;
import org.frankframework.frankdoc.util.LogUtil;
import org.junit.jupiter.api.Test;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EasyDocletTest {
	private static Logger log = LogUtil.getLogger(EasyDocletTest.class);

	@Test
	public void test() throws Exception {
		Set<? extends Element> typeElements = TestUtil.getIncludedElements("org.frankframework.frankdoc.testtarget.doclet");
		log.info("Elements that are type elements:");
		for (Element element : typeElements) {
			if (element instanceof TypeElement) {
				log.info("    " + ((TypeElement) element).getQualifiedName().toString());
			}
		}
		assertEquals(51, typeElements.size());
	}
}
