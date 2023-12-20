package org.frankframework.frankdoc.testtarget.doclet;

import org.frankframework.doc.ExcludeFromType;

@ExcludeFromType({Child.class, GrandChild.class})
public class WithAnnotationHavingClassArray {
}
