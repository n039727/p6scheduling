package au.com.wp.corp.p6.scheduling.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the GROUP_ROLE_MAPPING database table.
 * 
 */
@Entity
@Table(name="GROUP_ROLE_MAPPING")
@NamedQuery(name="GroupRoleMapping.findAll", query="SELECT g FROM GroupRoleMapping g")
public class GroupRoleMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="GRP_ROLE_ID")
	private long grpRoleId;

	@Column(name="GRP_NAM")
	private String grpNam;

	@Column(name="ROLE_NAM")
	private String roleNam;

	public GroupRoleMapping() {
	}

	public long getGrpRoleId() {
		return this.grpRoleId;
	}

	public void setGrpRoleId(long grpRoleId) {
		this.grpRoleId = grpRoleId;
	}

	public String getGrpNam() {
		return this.grpNam;
	}

	public void setGrpNam(String grpNam) {
		this.grpNam = grpNam;
	}

	public String getRoleNam() {
		return this.roleNam;
	}

	public void setRoleNam(String roleNam) {
		this.roleNam = roleNam;
	}

}