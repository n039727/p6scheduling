package au.com.wp.corp.p6.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the TODO_TEMPLATE database table.
 * 
 */
@Embeddable
public class TodoTemplatePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="TMPLT_ID")
	private long tmpltId;

	@Column(name="TODO_ID")
	private long todoId;

	public TodoTemplatePK() {
	}
	public long getTmpltId() {
		return this.tmpltId;
	}
	public void setTmpltId(long tmpltId) {
		this.tmpltId = tmpltId;
	}
	public long getTodoId() {
		return this.todoId;
	}
	public void setTodoId(long todoId) {
		this.todoId = todoId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TodoTemplatePK)) {
			return false;
		}
		TodoTemplatePK castOther = (TodoTemplatePK)other;
		return 
			(this.tmpltId == castOther.tmpltId)
			&& (this.todoId == castOther.todoId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.tmpltId ^ (this.tmpltId >>> 32)));
		hash = hash * prime + ((int) (this.todoId ^ (this.todoId >>> 32)));
		
		return hash;
	}
}