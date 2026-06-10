package org.frankframework.frankdoc.testtarget.examples.wrapper;

// Element name will be "MyWrapperPipe" (via addPipe(IPipe), stripping "Pipe" + role "Pipe").
// It inherited @FrankDocGroup = "Pipes" from IPipe, which should translate its components to "wrappers"
public class MyWrapperPipe implements IWrapperPipe {
}
