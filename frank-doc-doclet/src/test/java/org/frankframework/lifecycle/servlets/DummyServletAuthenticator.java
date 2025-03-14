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
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * More advanced documentation.
	 *
	 * {@value VALUE}
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public void setDoSomethingCool(String doSomethingCool) {
		this.doSomethingCool = doSomethingCool;
	}

}
