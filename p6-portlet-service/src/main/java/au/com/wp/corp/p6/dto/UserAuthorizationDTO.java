package au.com.wp.corp.p6.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class UserAuthorizationDTO {
	
	private String functionName;
	private boolean access;
	/**
	 * @return the functionName
	 */
	public String getFunctionName() {
		return functionName;
	}
	/**
	 * @param functionName the functionName to set
	 */
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	/**
	 * @return the access
	 */
	public boolean isAccess() {
		return access;
	}
	/**
	 * @param access the access to set
	 */
	public void setAccess(boolean access) {
		this.access = access;
	}
	
	
	

}
