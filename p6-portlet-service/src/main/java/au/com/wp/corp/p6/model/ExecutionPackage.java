package au.com.wp.corp.p6.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the EXECUTION_PACKAGE database table.
 * 
 */
@Entity
@Table(name="EXECUTION_PACKAGE")
@NamedQuery(name="ExecutionPackage.findAll", query="SELECT e FROM ExecutionPackage e")
public class ExecutionPackage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="EXECUTION_PACKAGE_EXCTNPCKGID_GENERATOR", sequenceName="EXCTN_PCKG_ID")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EXECUTION_PACKAGE_EXCTNPCKGID_GENERATOR")
	@Column(name="EXCTN_PCKG_ID")
	private long exctnPckgId;

	@Column(name="CRTD_TS")
	private Timestamp crtdTs;

	@Column(name="CRTD_USR")
	private String crtdUsr;

	@Column(name="EXCTN_PCKG_NAM")
	private String exctnPckgNam;

	@Column(name="LEAD_CREW_ID")
	private String leadCrewId;

	@Column(name="LST_UPDTD_TS")
	private Timestamp lstUpdtdTs;

	@Column(name="LST_UPDTD_USR")
	private String lstUpdtdUsr;

	//bi-directional many-to-one association to Task
	@OneToMany(mappedBy="executionPackage",cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	private List<Task> tasks;

	//bi-directional many-to-one association to TodoAssignment
	@OneToMany(mappedBy="executionPackage")
	private List<TodoAssignment> todoAssignments;

	public ExecutionPackage() {
	}

	public long getExctnPckgId() {
		return this.exctnPckgId;
	}

	public void setExctnPckgId(long exctnPckgId) {
		this.exctnPckgId = exctnPckgId;
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

	public String getExctnPckgNam() {
		return this.exctnPckgNam;
	}

	public void setExctnPckgNam(String exctnPckgNam) {
		this.exctnPckgNam = exctnPckgNam;
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

	public List<Task> getTasks() {
		return this.tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public Task addTask(Task task) {
		getTasks().add(task);
		task.setExecutionPackage(this);

		return task;
	}

	public Task removeTask(Task task) {
		getTasks().remove(task);
		task.setExecutionPackage(null);

		return task;
	}

	public List<TodoAssignment> getTodoAssignments() {
		return this.todoAssignments;
	}

	public void setTodoAssignments(List<TodoAssignment> todoAssignments) {
		this.todoAssignments = todoAssignments;
	}

	public TodoAssignment addTodoAssignment(TodoAssignment todoAssignment) {
		getTodoAssignments().add(todoAssignment);
		todoAssignment.setExecutionPackage(this);

		return todoAssignment;
	}

	public TodoAssignment removeTodoAssignment(TodoAssignment todoAssignment) {
		getTodoAssignments().remove(todoAssignment);
		todoAssignment.setExecutionPackage(null);

		return todoAssignment;
	}

}