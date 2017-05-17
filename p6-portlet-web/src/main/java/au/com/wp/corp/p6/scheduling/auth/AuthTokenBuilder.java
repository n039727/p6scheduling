/**
 * 
 */
package au.com.wp.corp.p6.scheduling.auth;

import java.security.Principal;

/**
 * @author n039619
 *
 */
public class AuthTokenBuilder {
	
	public String build(Principal userPrincipal) throws AuthException {
		validate(userPrincipal);
		String token = prepareToken(userPrincipal);
		String encryptedAuthToken = encryptToken(token);
		return encryptedAuthToken;
	}
	
	private void validate(Principal userPrincipal) throws AuthException {
		if (userPrincipal == null) {
			throw new AuthException("User Principal is null");
		}
	}
	
	private String prepareToken(Principal userPrincipal) {
		// TODO Implementation
		return userPrincipal.getName();
	}
	
	private String encryptToken(String token) {
		// TODO Implementation
		return token;
	}

}
