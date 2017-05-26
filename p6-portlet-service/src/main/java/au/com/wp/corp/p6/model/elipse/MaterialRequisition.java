package au.com.wp.corp.p6.model.elipse;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the MSF232 database table.
 * 
 */
@Entity
@Table(name="MSF232")
@NamedQuery(name="MaterialRequisition.findAll", query="SELECT m FROM MaterialRequisition m")
public class MaterialRequisition implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private MaterialRequisitionPK id;

	@Column(name="ACCT_CODE_TYPE")
	private String acctCodeType;

	@Column(name="ALLOC_DSTRCT")
	private String allocDstrct;

	@Column(name="ALLOC_PC")
	private BigDecimal allocPc;

	@Column(name="ALLOC_VAL_INV")
	private BigDecimal allocValInv;

	@Column(name="ALLOC_VAL_RCV")
	private BigDecimal allocValRcv;

	@Column(name="COST_DSTRCT_CDE")
	private String costDstrctCde;

	@Column(name="EQUIP_NO")
	private String equipNo;

	@Column(name="EXP_ELEMENT")
	private String expElement;

	@Column(name="FCAST_TO_GO")
	private BigDecimal fcastToGo;

	@Column(name="GL_ACCOUNT")
	private String glAccount;

	@Column(name="PROJECT_NO")
	private String projectNo;

	@Column(name="SUBLEDGER_ACCT")
	private String subledgerAcct;

	@Column(name="SUBLEDGER_TYPE")
	private String subledgerType;

	@Column(name="WORK_ORDER")
	private String workOrder;

	public MaterialRequisition() {
	}

	public MaterialRequisitionPK getId() {
		return this.id;
	}

	public void setId(MaterialRequisitionPK id) {
		this.id = id;
	}

	public String getAcctCodeType() {
		return this.acctCodeType;
	}

	public void setAcctCodeType(String acctCodeType) {
		this.acctCodeType = acctCodeType;
	}

	public String getAllocDstrct() {
		return this.allocDstrct;
	}

	public void setAllocDstrct(String allocDstrct) {
		this.allocDstrct = allocDstrct;
	}

	public BigDecimal getAllocPc() {
		return this.allocPc;
	}

	public void setAllocPc(BigDecimal allocPc) {
		this.allocPc = allocPc;
	}

	public BigDecimal getAllocValInv() {
		return this.allocValInv;
	}

	public void setAllocValInv(BigDecimal allocValInv) {
		this.allocValInv = allocValInv;
	}

	public BigDecimal getAllocValRcv() {
		return this.allocValRcv;
	}

	public void setAllocValRcv(BigDecimal allocValRcv) {
		this.allocValRcv = allocValRcv;
	}

	public String getCostDstrctCde() {
		return this.costDstrctCde;
	}

	public void setCostDstrctCde(String costDstrctCde) {
		this.costDstrctCde = costDstrctCde;
	}

	public String getEquipNo() {
		return this.equipNo;
	}

	public void setEquipNo(String equipNo) {
		this.equipNo = equipNo;
	}

	public String getExpElement() {
		return this.expElement;
	}

	public void setExpElement(String expElement) {
		this.expElement = expElement;
	}

	public BigDecimal getFcastToGo() {
		return this.fcastToGo;
	}

	public void setFcastToGo(BigDecimal fcastToGo) {
		this.fcastToGo = fcastToGo;
	}

	public String getGlAccount() {
		return this.glAccount;
	}

	public void setGlAccount(String glAccount) {
		this.glAccount = glAccount;
	}

	public String getProjectNo() {
		return this.projectNo;
	}

	public void setProjectNo(String projectNo) {
		this.projectNo = projectNo;
	}

	public String getSubledgerAcct() {
		return this.subledgerAcct;
	}

	public void setSubledgerAcct(String subledgerAcct) {
		this.subledgerAcct = subledgerAcct;
	}

	public String getSubledgerType() {
		return this.subledgerType;
	}

	public void setSubledgerType(String subledgerType) {
		this.subledgerType = subledgerType;
	}

	public String getWorkOrder() {
		return this.workOrder;
	}

	public void setWorkOrder(String workOrder) {
		this.workOrder = workOrder;
	}

}