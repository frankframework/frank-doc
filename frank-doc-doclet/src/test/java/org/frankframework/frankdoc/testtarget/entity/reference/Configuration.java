package org.frankframework.frankdoc.testtarget.entity.reference;

/**
 * The Configuration is placeholder of all configuration objects. Besides that, it provides
 * functions for starting and stopping adapters as a facade.
 * @author martijn
 */
public class Configuration {
    // TODO: Fix writing XML tags in JavaDoc comments, issue https://github.com/ibissource/frank-doc/issues/118
	/**
	 * This attribute should be allowed in element Configuration, but not in element Module.
	 * @param value
	 */
	public void setMyAttribute(String value) {
	}

	/**
	 * Register an adapter with the configuration.
	 */
	public void addAdapter(Adapter adapter) {
	}

	public void addConfigWarning(String message) {
	}
}
