package au.com.wp.corp.p6.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the TASK database table.
 * 
 */
@Entity
@NamedQuery(name="Task.findAll", query="SELECT t FROM Task t")
public class Task implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TASK_ID")
	private String taskId;

	private String cmts;

	@Column(name="CREW_ID")
	private String crewId;

	@Column(name="CRTD_TS")
	private Timestamp crtdTs;

	@Column(name="CRTD_USR")
	private String crtdUsr;

	@Column(name="DEPOT_ID")
	private String depotId;

	@Column(name="LEAD_CREW_ID")
	private String leadCrewId;

	@Column(name="LST_UPDTD_TS")
	private Timestamp lstUpdtdTs;

	@Column(name="LST_UPDTD_USR")
	private String lstUpdtdUsr;

	@Column(name="MATRL_REQ_REF")
	private String matrlReqRef;

	@Temporal(TemporalType.DATE)
	@Column(name="SCHD_DT")
	private Date schdDt;

	//bi-directional many-to-one association to ExecutionPackage
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="EXCTN_PCKG_ID")
	private ExecutionPackage executionPackage;

	//bi-directional many-to-one association to TodoAssignment

	@OneToMany(mappedBy="todoAssignMentPK.task",cascade = CascadeType.PERSIST, fetch = FetchType.EAGER, orphanRemoval=true)
	private Set<TodoAssignment> todoAssignments;
	
	@Column(name="ACTN_FLG")
	private String actioned = "N";


	public Task() {
	}

	public String getTaskId() {
		return this.taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getCmts() {
		return this.cmts;
	}

	public void setCmts(String cmts) {
		this.cmts = cmts;
	}

	public String getCrewId() {
		return this.crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
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

	public String getDepotId() {
		return this.depotId;
	}

	public void setDepotId(String depotId) {
		this.depotId = depotId;
	}

	public String getLeadCrewId() {
		return this.leadCrewId;
	}

	public void setLeadCrewId(String leadCrewId) {
		this.leadCrewId = leadCrewId;
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

	public String getMatrlReqRef() {
		return this.matrlReqRef;
	}

	public void setMatrlReqRef(String matrlReqRef) {
		this.matrlReqRef = matrlReqRef;
	}

	public Date getSchdDt() {
		return this.schdDt;
	}

	public void setSchdDt(Date schdDt) {
		this.schdDt = schdDt;
	}

	public ExecutionPackage getExecutionPackage() {
		return this.executionPackage;
	}

	public void setExecutionPackage(ExecutionPackage executionPackage) {
		this.executionPackage = executionPackage;
	}

	public Set<TodoAssignment> getTodoAssignments() {
		return this.todoAssignments;
	}

	public void setTodoAssignments(Set<TodoAssignment> todoAssignments) {
		this.todoAssignments = todoAssignments;
	}

	public TodoAssignment addTodoAssignment(TodoAssignment todoAssignment) {
		getTodoAssignments().add(todoAssignment);
		todoAssignment.getTodoAssignMentPK().setTask(this);

		return todoAssignment;
	}

	public TodoAssignment removeTodoAssignment(TodoAssignment todoAssignment) {
		getTodoAssignments().remove(todoAssignment);
		todoAssignment.getTodoAssignMentPK().setTask(null);

		return todoAssignment;
	}
	
	/**
	 * @return the actioned
	 */
	public String getActioned() {
		return actioned;
	}

	/**
	 * @param actioned the actioned to set
	 */
	public void setActioned(String actioned) {
		this.actioned = actioned;
	}

}