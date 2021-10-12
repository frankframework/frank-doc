package org.frankframework.frankdoc.wrapper;

import org.junit.Ignore;
import org.junit.Test;

import com.sun.javadoc.ClassDoc;

public class EasyDocletTest {
	@Ignore
	@Test
	public void test() throws Exception {
		for(ClassDoc c: TestUtil.getClassDocs("org.frankframework.frankdoc.testtarget.doclet")) {
			System.out.println(c.name());
		}
	}
}
