package au.com.wp.corp.p6.integration.dto;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Scope(value="request", proxyMode=ScopedProxyMode.TARGET_CLASS)
@Component
public class UserTokenRequest {
	String userPrincipal = "";
	
	public String getUserPrincipal() {
		if(userPrincipal == null || "".equals(userPrincipal)){
			userPrincipal = "P6 Test User";
		}
		return userPrincipal;
	}

	public void setUserPrincipal(String userPrincipal) {
		this.userPrincipal = userPrincipal;
	}
}
