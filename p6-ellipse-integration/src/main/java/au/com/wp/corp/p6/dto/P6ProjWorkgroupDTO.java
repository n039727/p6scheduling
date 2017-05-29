/**
 * 
 */
package au.com.wp.corp.p6.dto;

import java.io.Serializable;

/**
 * DTO object to hold all project resource mapping attributes
 * 
 * @author N039126
 * @version 1.0
 */
public class P6ProjWorkgroupDTO implements Serializable {

	private int projectObjectId;

	private String projectName;

	private String primaryResourceId;

	private String resourceName;

	private int roleId;

	private String roleName;

	private String schedulerinbox;

	private String primaryResourceYN;



	/**
	 * @return the projectObjectId
	 */
	public int getProjectObjectId() {
		return projectObjectId;
	}

	/**
	 * @param projectObjectId the projectObjectId to set
	 */
	public void setProjectObjectId(int projectObjectId) {
		this.projectObjectId = projectObjectId;
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @param projectName
	 *            the projectName to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * @return the primaryResourceId
	 */
	public String getPrimaryResourceId() {
		return primaryResourceId;
	}

	/**
	 * @param primaryResourceId
	 *            the primaryResourceId to set
	 */
	public void setPrimaryResourceId(String primaryResourceId) {
		this.primaryResourceId = primaryResourceId;
	}

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * @param resourceName
	 *            the resourceName to set
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	/**
	 * @return the roleId
	 */
	public int getRoleId() {
		return roleId;
	}

	/**
	 * @param roleId
	 *            the roleId to set
	 */
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName
	 *            the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the schedulerinbox
	 */
	public String getSchedulerinbox() {
		return schedulerinbox;
	}

	/**
	 * @param schedulerinbox
	 *            the schedulerinbox to set
	 */
	public void setSchedulerinbox(String schedulerinbox) {
		this.schedulerinbox = schedulerinbox;
	}

	/**
	 * @return the primaryResourceYN
	 */
	public String getPrimaryResourceYN() {
		return primaryResourceYN;
	}

	/**
	 * @param primaryResourceYN
	 *            the primaryResourceYN to set
	 */
	public void setPrimaryResourceYN(String primaryResourceYN) {
		this.primaryResourceYN = primaryResourceYN;
	}

}
