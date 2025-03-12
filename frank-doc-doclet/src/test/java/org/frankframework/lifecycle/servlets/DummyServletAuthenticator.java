package org.frankframework.lifecycle.servlets;

/**
 * Sample documentation
 *
 * {@value VALUE}
 */
public class DummyServletAuthenticator implements IAuthenticator {

	private String username = null;
	private String password = null;
	private String doSomethingCool = null;

	private static final String VALUE = "dummy";

	/**
	 * Documentation for the username!
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * More advanced documentation.
	 *
	 * {@value VALUE}
	 */
	public String getPassword() {
		return this.password;
	}

	public String getDoSomethingCool() {
		return this.doSomethingCool;
	}

}
