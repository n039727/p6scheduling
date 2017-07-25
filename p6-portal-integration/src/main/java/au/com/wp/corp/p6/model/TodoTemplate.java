package au.com.wp.corp.p6.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
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

	@Column(name="TMPLT_ID")
	private long tmpltId;
	
	@Id
	@SequenceGenerator(name="TODO_ID_GENERATOR", sequenceName="TODO_ID",initialValue=50,allocationSize= 50)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TODO_ID_GENERATOR")
	@Column(name="TODO_ID")
	private long todoId;

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

	@Column(name="TODO_NAM")
	private String todoNam;

	@Column(name="TYP_ID")
	private java.math.BigDecimal typId;

	public TodoTemplate() {
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

	public String getTodoNam() {
		return this.todoNam;
	}

	public void setTodoNam(String todoNam) {
		this.todoNam = todoNam;
	}

	public java.math.BigDecimal getTypId() {
		return this.typId;
	}

	public void setTypId(java.math.BigDecimal typId) {
		this.typId = typId;
	}

	public long getTmpltId() {
		return tmpltId;
	}

	public void setTmpltId(long tmpltId) {
		this.tmpltId = tmpltId;
	}

	public long getTodoId() {
		return todoId;
	}

	public void setTodoId(long todoId) {
		this.todoId = todoId;
	}

}