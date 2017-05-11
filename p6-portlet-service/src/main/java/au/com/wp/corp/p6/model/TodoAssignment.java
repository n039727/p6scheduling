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
	/**
	 * @Id @SequenceGenerator(name="TODO_ASSIGNMENT_ASIGNMTID_GENERATOR",
	 *     sequenceName="ASIGNMT_ID" , allocationSize=1)
	 * @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TODO_ASSIGNMENT_ASIGNMTID_GENERATOR") @Column(name="ASIGNMT_ID")
	 *                                                   private long asignmtId;
	 **/
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

	// bi-directional many-to-one association to ExecutionPackage
	//@ManyToOne
	//@JoinColumn(name = "EXCTN_PCKG_ID")
	//private ExecutionPackage executionPackage;


	// bi-directional many-to-one association to TodoTemplate
	//@ManyToOne
	//@JoinColumn(name = "TMPLT_ID", insertable = false, updatable = false)
//	private TodoTemplate todoTemplate;

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


}
