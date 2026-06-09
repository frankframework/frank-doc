package org.frankframework.frankdoc.testtarget.examples.wrapper;

import org.frankframework.doc.FrankDocGroup;
import org.frankframework.doc.FrankDocGroupValue;

// No @Components annotation: inherits "Pipes" from IPipe, just like the real IWrapperPipe
@FrankDocGroup(FrankDocGroupValue.WRAPPER)
public interface IWrapperPipe extends IPipe {
}
