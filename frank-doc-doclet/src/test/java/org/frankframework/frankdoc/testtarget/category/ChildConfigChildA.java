package org.frankframework.frankdoc.testtarget.category;

import org.frankframework.frankdoc.testtarget.category.doc.CategoryA;
import org.frankframework.frankdoc.testtarget.category.doc.CategoryAValue;
import org.frankframework.frankdoc.testtarget.category.doc.CategoryB;
import org.frankframework.frankdoc.testtarget.category.doc.CategoryBValue;
import org.frankframework.frankdoc.testtarget.category.doc.CategoryC;
import org.frankframework.frankdoc.testtarget.category.doc.CategoryCValue;

@CategoryA(value = CategoryAValue.CategoryAValue_2)
@CategoryB(value = CategoryBValue.CategoryBValue_2)
@CategoryC(value = CategoryCValue.CategoryCValue_2)
public class ChildConfigChildA extends ChildConfigParent implements IChild {
}
