package au.com.wp.corp.p6.dto;

import org.springframework.stereotype.Component;

//@Scope(value="request", proxyMode=ScopedProxyMode.TARGET_CLASS)
@Component
public class UserTokenRequest {
	String userPrincipal = "";
	
	public String getUserPrincipal() {
		return userPrincipal;
	}

	public void setUserPrincipal(String userPrincipal) {
		this.userPrincipal = userPrincipal;
	}
}
