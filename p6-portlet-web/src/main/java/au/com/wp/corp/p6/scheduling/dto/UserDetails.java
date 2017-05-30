/**
 * 
 */
package au.com.wp.corp.p6.scheduling.dto;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author n039619
 *
 */
@JsonInclude(Include.NON_NULL)
public class UserDetails implements Serializable {
	
	private String userName;
	private Map<String, UserAuthorizationDTO> accessMap;
	
	
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
	
	
}
