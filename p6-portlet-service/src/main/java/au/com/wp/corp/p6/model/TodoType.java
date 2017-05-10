package au.com.wp.corp.p6.model;

import java.io.Serializable;
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
 * The persistent class for the TODO_TYPE database table.
 * 
 */
@Entity
@Table(name="TODO_TYPE")
@NamedQuery(name="TodoType.findAll", query="SELECT t FROM TodoType t")
public class TodoType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="TODO_TYPE_TYPID_GENERATOR", sequenceName="TODO_TYPE_ID")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TODO_TYPE_TYPID_GENERATOR")
	@Column(name="TYP_ID")
	private long typId;

	@Column(name="CRTD_TS")
	private Timestamp crtdTs;

	@Column(name="CRTD_USR")
	private String crtdUsr;

	@Column(name="LST_UPDTD_TS")
	private Timestamp lstUpdtdTs;

	@Column(name="LST_UPDTD_USR")
	private String lstUpdtdUsr;

	@Column(name="TYP_DESC")
	private String typDesc;

	//bi-directional many-to-one association to TodoTemplate
	//@OneToMany(mappedBy="todoType")
	//private List<TodoTemplate> todoTemplates;

	public TodoType() {
	}

	public long getTypId() {
		return this.typId;
	}

	public void setTypId(long typId) {
		this.typId = typId;
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

	public String getTypDesc() {
		return this.typDesc;
	}

	public void setTypDesc(String typDesc) {
		this.typDesc = typDesc;
	}
/**
	public List<TodoTemplate> getTodoTemplates() {
		return this.todoTemplates;
	}

	public void setTodoTemplates(List<TodoTemplate> todoTemplates) {
		this.todoTemplates = todoTemplates;
	}

	public TodoTemplate addTodoTemplate(TodoTemplate todoTemplate) {
		getTodoTemplates().add(todoTemplate);
		todoTemplate.setTodoType(this);

		return todoTemplate;
	}

	public TodoTemplate removeTodoTemplate(TodoTemplate todoTemplate) {
		getTodoTemplates().remove(todoTemplate);
		todoTemplate.setTodoType(null);

		return todoTemplate;
	}
**/
}