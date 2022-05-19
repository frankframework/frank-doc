package org.frankframework.frankdoc.testtarget.entity.reference;

/**
 * The Configuration is placeholder of all configuration objects. Besides that, it provides
 * functions for starting and stopping adapters as a facade.
 * @author martijn
 */
public class Configuration {
	/**
	 * This attribute should be allowed in <Configuration>, but not <Module>
	 * @param value
	 */
	public void setMyAttribute(String value) {
	}

	/**
	 * Register an adapter with the configuration.
	 */
	public void registerAdapter(Adapter adapter) {
	}
}
