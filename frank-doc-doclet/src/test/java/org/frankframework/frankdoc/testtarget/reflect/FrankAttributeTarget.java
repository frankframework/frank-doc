package org.frankframework.frankdoc.testtarget.reflect;
import java.util.List;

public class FrankAttributeTarget extends FrankAttributeTargetParent {
	public static enum FrankAttributeTargetEnum {ONE, TWO};

	public void setAttributeSetterGetter(String value) {
	}

	public String getAttributeSetterGetter() {
		return null;
	}

	public void setAttributeSetterIs(boolean value) {
	}

	public boolean isAttributeSetterIs() {
		return true;
	}

	public void setAttributeOnlySetter(String value) {
	}

	public void setAttributeVararg(String... value) {
	}

	public void setAttributeOnlySetterInt(int value) {
	}

	public void setAttributeOnlySetterIntBoxed(Integer value) {
	}

	public void setAttributeOnlySetterBoolBoxed(Boolean value) {
	}

	public void setAttributeOnlySetterLongBoxed(Long value) {
	}

	public void setAttributeOnlySetterByteBoxed(Byte value) {
	}

	public void setAttributeOnlySetterShortBoxed(Short value) {
	}

	public void setNoAttributeComplexType(List<String> value) {
	}

	public List<String> getNoAttributeComplexType() {
		return null;
	}

	public String prefix() {
		return null;
	}

	public String get() {
		return null;
	}

	public void setInvalidSetter(String s, int i) {
	}

	public void setInvalidSetterNoParams() {
	}

	public void setIbisDockedOnlyDescription(String value) {
	}

	@Deprecated
	public void setIbisDockedDeprecated(String value) {
	}

	/**
	 * Attribute with JavaDoc
	 */
	public void setAttributeWithJavaDoc(String value) {
	}

	@Override
	public void setAttributeWithInheritedJavaDoc(String value) {
	}

	/**
	 * @ff.default My default value
	 */
	public void setAttributeWithJavaDocDefault(String value) {
	}

	@Override
	public void setAttributeWithInheritedJavaDocDefault(String value) {
	}

	/**
	 * @ff.default My overruled default value
	 */
	public void setAttributeWithIbisDocThatOverrulesJavadocDefault(String value) {
	}

	public FrankAttributeTargetEnum getEnumAttributeWithInvalidDefaultEnum() {
		return null;
	}

	public void setAttributeSetterTakingEnum(AttributeSetterArgumentEnum arg) {
	}
}
