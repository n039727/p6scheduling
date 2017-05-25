package au.com.wp.corp.p6.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the PORTAL_FUNCTION database table.
 * 
 */
@Entity
@Table(name="PORTAL_FUNCTION")
@NamedQuery(name="PortalFunction.findAll", query="SELECT p FROM PortalFunction p")
public class PortalFunction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="FUNC_ID")
	private long funcId;

	@Column(name="FUNC_DESC")
	private String funcDesc;

	@Column(name="FUNC_NAM")
	private String funcNam;

	//bi-directional many-to-one association to FunctionAccess
	@OneToMany(mappedBy="portalFunction")
	private List<FunctionAccess> functionAccesses;

	public PortalFunction() {
	}

	public long getFuncId() {
		return this.funcId;
	}

	public void setFuncId(long funcId) {
		this.funcId = funcId;
	}

	public String getFuncDesc() {
		return this.funcDesc;
	}

	public void setFuncDesc(String funcDesc) {
		this.funcDesc = funcDesc;
	}

	public String getFuncNam() {
		return this.funcNam;
	}

	public void setFuncNam(String funcNam) {
		this.funcNam = funcNam;
	}

	public List<FunctionAccess> getFunctionAccesses() {
		return this.functionAccesses;
	}

	public void setFunctionAccesses(List<FunctionAccess> functionAccesses) {
		this.functionAccesses = functionAccesses;
	}

	public FunctionAccess addFunctionAccess(FunctionAccess functionAccess) {
		getFunctionAccesses().add(functionAccess);
		functionAccess.setPortalFunction(this);

		return functionAccess;
	}

	public FunctionAccess removeFunctionAccess(FunctionAccess functionAccess) {
		getFunctionAccesses().remove(functionAccess);
		functionAccess.setPortalFunction(null);

		return functionAccess;
	}

}