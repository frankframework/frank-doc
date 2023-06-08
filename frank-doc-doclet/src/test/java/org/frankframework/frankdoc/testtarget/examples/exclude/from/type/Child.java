package org.frankframework.frankdoc.testtarget.examples.exclude.from.type;

/**
 * Should not appear in the type of IInterface, because we extend
 * from Parent and that class has been excluded.
 * @author martijn
 *
 */
public class Child extends Parent {
}
