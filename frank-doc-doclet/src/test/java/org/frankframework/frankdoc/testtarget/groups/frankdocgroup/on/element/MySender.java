package org.frankframework.frankdoc.testtarget.groups.frankdocgroup.on.element;

import nl.nn.adapterframework.doc.FrankDocGroup;

// Setting the order here is not realistic, but we have to test it because syntax allows it.
// Causes MySender to be in "Senders" exclusively
@FrankDocGroup(name = "Senders", order = 20)
public class MySender extends MyListener implements ISender {
}
