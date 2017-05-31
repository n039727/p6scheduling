/**
 * 
 */
package au.com.wp.corp.p6.scheduling.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author n039619
 *
 */
@JsonInclude(Include.NON_NULL)
public class UserDetails implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private List<String> roles;
	private Map<String, UserAuthorizationDTO> accessMap;
	private boolean isAuthEnabled;
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Map<String, UserAuthorizationDTO> getAccessMap() {
		return accessMap;
	}
	public void setAccessMap(Map<String, UserAuthorizationDTO> accessMap) {
		this.accessMap = accessMap;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public boolean isAuthEnabled() {
		return isAuthEnabled;
	}
	public void setAuthEnabled(boolean isAuthEnabled) {
		this.isAuthEnabled = isAuthEnabled;
	}
	
	
}
