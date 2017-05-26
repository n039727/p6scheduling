package au.com.wp.corp.p6.model.elipse;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the MSF232 database table.
 * 
 */
@Embeddable
public class MaterialRequisitionPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="DSTRCT_CODE")
	private String dstrctCode;

	@Column(name="REQ_232_TYPE")
	private String req232Type;

	@Column(name="REQUISITION_NO")
	private String requisitionNo;

	@Column(name="ALLOC_COUNT")
	private String allocCount;

	public MaterialRequisitionPK() {
	}
	public String getDstrctCode() {
		return this.dstrctCode;
	}
	public void setDstrctCode(String dstrctCode) {
		this.dstrctCode = dstrctCode;
	}
	public String getReq232Type() {
		return this.req232Type;
	}
	public void setReq232Type(String req232Type) {
		this.req232Type = req232Type;
	}
	public String getRequisitionNo() {
		return this.requisitionNo;
	}
	public void setRequisitionNo(String requisitionNo) {
		this.requisitionNo = requisitionNo;
	}
	public String getAllocCount() {
		return this.allocCount;
	}
	public void setAllocCount(String allocCount) {
		this.allocCount = allocCount;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MaterialRequisitionPK)) {
			return false;
		}
		MaterialRequisitionPK castOther = (MaterialRequisitionPK)other;
		return 
			this.dstrctCode.equals(castOther.dstrctCode)
			&& this.req232Type.equals(castOther.req232Type)
			&& this.requisitionNo.equals(castOther.requisitionNo)
			&& this.allocCount.equals(castOther.allocCount);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.dstrctCode.hashCode();
		hash = hash * prime + this.req232Type.hashCode();
		hash = hash * prime + this.requisitionNo.hashCode();
		hash = hash * prime + this.allocCount.hashCode();
		
		return hash;
	}
}