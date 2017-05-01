package au.com.wp.corp.p6.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the TODO_ASSIGNMENT database table.
 * 
 */
@Entity
@Table(name="TODO_ASSIGNMENT")
@NamedQuery(name="TodoAssignment.findAll", query="SELECT t FROM TodoAssignment t")
public class TodoAssignment implements Serializable {
	private static final long serialVersionUID = 1L;
/**
	@Id
	@SequenceGenerator(name="TODO_ASSIGNMENT_ASIGNMTID_GENERATOR", sequenceName="ASIGNMT_ID" , allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TODO_ASSIGNMENT_ASIGNMTID_GENERATOR")
	@Column(name="ASIGNMT_ID")
	private long asignmtId;
**/
	@EmbeddedId
	private TodoAssignmentPK todoAssignMentPK =  new TodoAssignmentPK();
	
	private String cmts;

	@Column(name="CRTD_TS")
	private Timestamp crtdTs;

	@Column(name="CRTD_USR")
	private String crtdUsr;

	@Column(name="LST_UPDTD_TS")
	private Timestamp lstUpdtdTs;

	@Column(name="LST_UPDTD_USR")
	private String lstUpdtdUsr;

	@Temporal(TemporalType.DATE)
	@Column(name="REQD_BY_DT")
	private Date reqdByDt;

	@Column(name="STAT")
	private String stat;

	@Column(name="SUPRTNG_DOC_LNK")
	private String suprtngDocLnk;

	@Column(name="TODO_ID")
	private BigDecimal todoId;

	//bi-directional many-to-one association to ExecutionPackage
	@ManyToOne
	@JoinColumn(name="EXCTN_PCKG_ID")
	private ExecutionPackage executionPackage;

	//bi-directional many-to-one association to Task
	@Column(name="TASK_ID")
	private Task task;

	//bi-directional many-to-one association to TodoTemplate
	@ManyToOne
	@JoinColumn(name="TMPLT_ID", insertable=false, updatable=false)
	private TodoTemplate todoTemplate;

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

	public BigDecimal getTodoId() {
		return this.todoId;
	}

	public void setTodoId(BigDecimal todoId) {
		this.todoId = todoId;
	}

	public ExecutionPackage getExecutionPackage() {
		return this.executionPackage;
	}

	public void setExecutionPackage(ExecutionPackage executionPackage) {
		this.executionPackage = executionPackage;
	}

	public Task getTask() {
		return this.task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	/**
	 * @return the todoTemplate
	 */
	public TodoTemplate getTodoTemplate() {
		return todoTemplate;
	}

	/**
	 * @param todoTemplate the todoTemplate to set
	 */
	public void setTodoTemplate(TodoTemplate todoTemplate) {
		this.todoTemplate = todoTemplate;
	}



	@Embeddable
	private class TodoAssignmentPK implements Serializable{
		
		private long exctn_Pckg_Id;

		private String task_Id;

		private long tmplt_Id;

		/**
		 * @return the exctn_Pckg_Id
		 */
		public long getExctn_Pckg_Id() {
			return exctn_Pckg_Id;
		}

		/**
		 * @param exctn_Pckg_Id the exctn_Pckg_Id to set
		 */
		public void setExctn_Pckg_Id(long exctn_Pckg_Id) {
			this.exctn_Pckg_Id = exctn_Pckg_Id;
		}

		/**
		 * @return the task_Id
		 */
		public String getTask_Id() {
			return task_Id;
		}

		/**
		 * @param task_Id the task_Id to set
		 */
		public void setTask_Id(String task_Id) {
			this.task_Id = task_Id;
		}

		/**
		 * @return the tmplt_Id
		 */
		public long getTmplt_Id() {
			return tmplt_Id;
		}

		/**
		 * @param tmplt_Id the tmplt_Id to set
		 */
		public void setTmplt_Id(long tmplt_Id) {
			this.tmplt_Id = tmplt_Id;
		}


	}
}

