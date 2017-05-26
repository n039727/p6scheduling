package au.com.wp.corp.p6.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the FUNCTION_ACCESS database table.
 * 
 */
@Entity
@Table(name="FUNCTION_ACCESS")
@NamedQuery(name="FunctionAccess.findAll", query="SELECT f FROM FunctionAccess f")
public class FunctionAccess implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="FUNC_ACCESS_ID")
	private long funcAccessId;

	@Column(name="ROLE_NAM")
	private String roleNam;

	@Column(name="WRITE_FLG")
	private String writeFlg;

	//bi-directional many-to-one association to PortalFunction
	@ManyToOne
	@JoinColumn(name="FUNC_ID")
	private PortalFunction portalFunction;

	public FunctionAccess() {
	}

	public long getFuncAccessId() {
		return this.funcAccessId;
	}

	public void setFuncAccessId(long funcAccessId) {
		this.funcAccessId = funcAccessId;
	}

	public String getRoleNam() {
		return this.roleNam;
	}

	public void setRoleNam(String roleNam) {
		this.roleNam = roleNam;
	}

	public String getWriteFlg() {
		return this.writeFlg;
	}

	public void setWriteFlg(String writeFlg) {
		this.writeFlg = writeFlg;
	}

	public PortalFunction getPortalFunction() {
		return this.portalFunction;
	}

	public void setPortalFunction(PortalFunction portalFunction) {
		this.portalFunction = portalFunction;
	}

}