package org.frankframework.frankdoc.testtarget.doclet;

import nl.nn.adapterframework.doc.ExcludeFromType;

@ExcludeFromType({Child.class, GrandChild.class})
public class WithAnnotationHavingClassArray {
}
