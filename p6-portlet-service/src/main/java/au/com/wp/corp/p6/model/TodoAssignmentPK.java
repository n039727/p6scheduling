/**
 * 
 */
package au.com.wp.corp.p6.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author n039126
 *
 */
@Embeddable
public class TodoAssignmentPK implements Serializable {

	@Column(name = "TODO_ID")
	private BigDecimal todoId;

	// bi-directional many-to-one association to Task
	@ManyToOne
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

}


