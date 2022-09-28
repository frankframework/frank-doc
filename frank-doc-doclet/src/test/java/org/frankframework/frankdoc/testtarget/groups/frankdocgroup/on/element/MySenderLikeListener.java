package org.frankframework.frankdoc.testtarget.groups.frankdocgroup.on.element;

import nl.nn.adapterframework.doc.FrankDocGroup;

// We overrule the @FrankDocGroup we inherit from MySender.
@FrankDocGroup(name = "Listeners")
public class MySenderLikeListener extends MySender {
}
