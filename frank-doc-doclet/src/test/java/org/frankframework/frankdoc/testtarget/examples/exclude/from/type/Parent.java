package org.frankframework.frankdoc.testtarget.examples.exclude.from.type;

import org.frankframework.doc.ExcludeFromType;

/**
 * Should not appear in the type of IInterface.
 * @author martijn
 *
 */
@ExcludeFromType(IInterface.class)
public class Parent implements IInterface {
}
