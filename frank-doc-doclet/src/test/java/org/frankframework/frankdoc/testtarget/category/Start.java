package org.frankframework.frankdoc.testtarget.category;

import org.frankframework.frankdoc.testtarget.category.doc.CategoryA;
import org.frankframework.frankdoc.testtarget.category.doc.CategoryAValue;
import org.frankframework.frankdoc.testtarget.category.doc.CategoryB;
import org.frankframework.frankdoc.testtarget.category.doc.CategoryBValue;
import org.frankframework.frankdoc.testtarget.category.doc.CategoryC;
import org.frankframework.frankdoc.testtarget.category.doc.CategoryCValue;

@CategoryA(value = CategoryAValue.CategoryAValue_1)
@CategoryB(value = CategoryBValue.CategoryBValue_1)

@CategoryC(value = CategoryCValue.CategoryCValue_3)
@CategoryC(value = CategoryCValue.CategoryCValue_4)
public class Start extends ParentOfStart {

	public void setIChild(IChild c) {
	}

}
