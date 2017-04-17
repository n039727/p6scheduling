package au.com.wp.corp.p6.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
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

	@Id
	@SequenceGenerator(name="TODO_ASSIGNMENT_ASIGNMTID_GENERATOR", sequenceName="ASIGNMT_ID")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TODO_ASSIGNMENT_ASIGNMTID_GENERATOR")
	@Column(name="ASIGNMT_ID")
	private long asignmtId;

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
	@ManyToOne
	@JoinColumn(name="TASK_ID")
	private Task task;

	//bi-directional many-to-one association to TodoTemplate
	@ManyToOne
	@JoinColumn(name="TMPLT_ID")
	private TodoTemplate todoTemplate;

	public TodoAssignment() {
	}

	public long getAsignmtId() {
		return this.asignmtId;
	}

	public void setAsignmtId(long asignmtId) {
		this.asignmtId = asignmtId;
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

	public TodoTemplate getTodoTemplate() {
		return this.todoTemplate;
	}

	public void setTodoTemplate(TodoTemplate todoTemplate) {
		this.todoTemplate = todoTemplate;
	}

}