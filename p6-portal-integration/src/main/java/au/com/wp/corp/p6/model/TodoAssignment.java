package au.com.wp.corp.p6.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the TODO_ASSIGNMENT database table.
 * 
 */
@Entity
@Table(name = "TODO_ASSIGNMENT")
@NamedQuery(name = "TodoAssignment.findAll", query = "SELECT t FROM TodoAssignment t")
public class TodoAssignment implements Serializable {
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	private TodoAssignmentPK todoAssignMentPK = new TodoAssignmentPK();

	private String cmts;

	@Column(name = "CRTD_TS")
	private Timestamp crtdTs;

	@Column(name = "CRTD_USR")
	private String crtdUsr;

	@Column(name = "LST_UPDTD_TS")
	private Timestamp lstUpdtdTs;

	@Column(name = "LST_UPDTD_USR")
	private String lstUpdtdUsr;

	@Temporal(TemporalType.DATE)
	@Column(name = "REQD_BY_DT")
	private Date reqdByDt;

	@Column(name = "STAT")
	private String stat;

	@Column(name = "SUPRTNG_DOC_LNK")
	private String suprtngDocLnk;

	/**
	 * @return the todoAssignMentPK
	 */
	public TodoAssignmentPK getTodoAssignMentPK() {
		return todoAssignMentPK;
	}

	/**
	 * @param todoAssignMentPK the todoAssignMentPK to set
	 */
	public void setTodoAssignMentPK(TodoAssignmentPK todoAssignMentPK) {
		this.todoAssignMentPK = todoAssignMentPK;
	}

	/**
	 * Empty constructor
	 */
	public TodoAssignment() {
	}

	public String getCmts() {
		return this.cmts;
	}

	public void setCmts(String cmts) {
		this.cmts = cmts;
	}

	public Timestamp getCrtdTs() {
		return this.crtdTs;
	}

	public void setCrtdTs(Timestamp crtdTs) {
		this.crtdTs = crtdTs;
	}

	public String getCrtdUsr() {
		return this.crtdUsr;
	}

	public void setCrtdUsr(String crtdUsr) {
		this.crtdUsr = crtdUsr;
	}

	public Timestamp getLstUpdtdTs() {
		return this.lstUpdtdTs;
	}

	public void setLstUpdtdTs(Timestamp lstUpdtdTs) {
		this.lstUpdtdTs = lstUpdtdTs;
	}

	public String getLstUpdtdUsr() {
		return this.lstUpdtdUsr;
	}

	public void setLstUpdtdUsr(String lstUpdtdUsr) {
		this.lstUpdtdUsr = lstUpdtdUsr;
	}

	public Date getReqdByDt() {
		return this.reqdByDt;
	}

	public void setReqdByDt(Date reqdByDt) {
		this.reqdByDt = reqdByDt;
	}

	public String getStat() {
		return this.stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getSuprtngDocLnk() {
		return this.suprtngDocLnk;
	}

	public void setSuprtngDocLnk(String suprtngDocLnk) {
		this.suprtngDocLnk = suprtngDocLnk;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((todoAssignMentPK == null) ? 0 : todoAssignMentPK.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TodoAssignment other = (TodoAssignment) obj;
		if (todoAssignMentPK == null) {
			if (other.todoAssignMentPK != null)
				return false;
		} else if (!todoAssignMentPK.equals(other.todoAssignMentPK))
			return false;
		return true;
	}


}
