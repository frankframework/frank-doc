package org.frankframework.credentialprovider;

/**
 * <p>CredentialFactory that reads its credentials from a plain (unencrypted) .properties file.</p>
 *
 * <p>Secret are stored in the properties file as key/value pairs, where the key is the alias and the value is the password.</p>
 *
 * @ff.info For adequate privacy in production environments, the source file should not be readable by unauthorised users.
 * @ff.info See the official <a href="https://docs.oracle.com/cd/E23095_01/Platform.93/ATGProgGuide/html/s0204propertiesfileformat01.html">Java Properties file format</a> for information about escaping and special characters.
 *
 * @author Gerrit van Brakel
 */
public class TestCredentialFactory implements ISecretProvider {
}
