/**
 * 
 */
package au.com.wp.corp.p6.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author n039126
 *
 */
@Embeddable
public class TodoAssignmentPK implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "TODO_ID")
	private BigDecimal todoId;

	// bi-directional many-to-one association to Task
	@ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name = "TASK_ID")
	private Task task;

	public Task getTask() {
		return this.task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public BigDecimal getTodoId() {
		return this.todoId;
	}

	public void setTodoId(BigDecimal todoId) {
		this.todoId = todoId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((task == null) ? 0 : task.hashCode());
		result = prime * result + ((todoId == null) ? 0 : todoId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TodoAssignmentPK other = (TodoAssignmentPK) obj;
		if (task == null) {
			if (other.task != null)
				return false;
		} else if (!task.equals(other.task))
			return false;
		if (todoId == null) {
			if (other.todoId != null)
				return false;
		} else if (!todoId.equals(other.todoId))
			return false;
		return true;
	}
}


