package org.frankframework.frankdoc.wrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.junit.jupiter.api.Test;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class EasyDocletTest {

	@Test
	public void test() throws Exception {
		Set<? extends Element> typeElements = TestUtil.getIncludedElements("org.frankframework.frankdoc.testtarget.doclet");
		log.info("Elements that are type elements:");
		for (Element element : typeElements) {
			if (element instanceof TypeElement) {
				log.info("    " + ((TypeElement) element).getQualifiedName());
			}
		}
		assertEquals(51, typeElements.size());
	}
}
