package org.frankframework.frankdoc.testtarget.examples.simple;

import org.frankframework.doc.EnumLabel;
import org.frankframework.doc.FrankDocGroup;
import org.frankframework.doc.FrankDocGroupValue;

/**
 * Description of Start. Value of VARIABLE is {@value #VARIABLE}. Value of TheEnum.ENUM_CONSTANT is {@value org.frankframework.frankdoc.testtarget.examples.simple.Start.TheEnum#ENUM_CONSTANT}.
 * Value of literal is {@literal This is a literal}.
 * @author M66C303
 *
 */
@FrankDocGroup(FrankDocGroupValue.BATCH)
public class Start extends AbstractParentOfStart {
	public static final String VARIABLE = "variable value";

	public enum TheEnum {
		@EnumLabel("enum constant")
		ENUM_CONSTANT;
	}

	public void setIChild(IChild child) {
	}

	public void addTChild(TChild child) {
	}

	public void setAttribute(String value) {
	}
}
