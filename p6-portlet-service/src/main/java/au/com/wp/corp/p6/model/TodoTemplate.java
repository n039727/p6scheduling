package au.com.wp.corp.p6.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the TODO_TEMPLATE database table.
 * 
 */
@Entity
@Table(name="TODO_TEMPLATE")
@NamedQuery(name="TodoTemplate.findAll", query="SELECT t FROM TodoTemplate t")
public class TodoTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TODO_TEMPLATE_TMPLTID_GENERATOR", sequenceName="TMPLT_ID")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TODO_TEMPLATE_TMPLTID_GENERATOR")
	@Column(name="TMPLT_ID")
	private long tmpltId;

	@Column(name="CRTD_TS")
	private Timestamp crtdTs;

	@Column(name="CRTD_USR")
	private String crtdUsr;

	@Column(name="LST_UPDTD_TS")
	private Timestamp lstUpdtdTs;

	@Column(name="LST_UPDTD_USR")
	private String lstUpdtdUsr;

	@Column(name="TMPLT_DESC")
	private String tmpltDesc;

	@Column(name="TODO_ID")
	private BigDecimal todoId;

	@Column(name="TODO_NAM")
	private String todoNam;
	
	@Column(name="TYP_ID")
	private long typeId;

	//bi-directional many-to-one association to TodoAssignment
	//@OneToMany(mappedBy="todoTemplate")
	//private List<TodoAssignment> todoAssignments;

	//bi-directional many-to-one association to TodoType
	//@ManyToOne
	//@JoinColumn(name="TYP_ID")
	//private TodoType todoType;

	public TodoTemplate() {
	}

	public long getTmpltId() {
		return this.tmpltId;
	}

	public void setTmpltId(long tmpltId) {
		this.tmpltId = tmpltId;
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

	public String getTmpltDesc() {
		return this.tmpltDesc;
	}

	public void setTmpltDesc(String tmpltDesc) {
		this.tmpltDesc = tmpltDesc;
	}

	public BigDecimal getTodoId() {
		return this.todoId;
	}

	public void setTodoId(BigDecimal todoId) {
		this.todoId = todoId;
	}

	public String getTodoNam() {
		return this.todoNam;
	}

	public void setTodoNam(String todoNam) {
		this.todoNam = todoNam;
	}

	/**
	 * @return the typeId
	 */
	public long getTypeId() {
		return typeId;
	}

	/**
	 * @param typeId the typeId to set
	 */
	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}

	/**
	public List<TodoAssignment> getTodoAssignments() {
		return this.todoAssignments;
	}

	public void setTodoAssignments(List<TodoAssignment> todoAssignments) {
		this.todoAssignments = todoAssignments;
	}
	
	public TodoAssignment addTodoAssignment(TodoAssignment todoAssignment) {
		getTodoAssignments().add(todoAssignment);
		todoAssignment.setTodoTemplate(this);

		return todoAssignment;
	}

	public TodoAssignment removeTodoAssignment(TodoAssignment todoAssignment) {
		getTodoAssignments().remove(todoAssignment);
		todoAssignment.setTodoTemplate(null);

		return todoAssignment;
	}


	public TodoType getTodoType() {
		return this.todoType;
	}

	public void setTodoType(TodoType todoType) {
		this.todoType = todoType;
	}
	
	**/

}