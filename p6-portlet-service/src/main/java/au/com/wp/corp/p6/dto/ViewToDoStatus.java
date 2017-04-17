/**
 * 
 */
package au.com.wp.corp.p6.dto;

import java.util.List;

/**
 * @author N039603
 *
 */
public class ViewToDoStatus {

	private String executionPackage;
	private String workOrders;
	private String scheduleDate;
	private String crewAssigned;
	private String leadCrew;
	private String schedulingComment;
	private String deportComment;
	private List<ToDoAssignment> todoAssignments;
	public String getExecutionPackage() {
		return executionPackage;
	}
	public void setExecutionPackage(String executionPackage) {
		this.executionPackage = executionPackage;
	}
	public String getWorkOrders() {
		return workOrders;
	}
	public void setWorkOrders(String workOrders) {
		this.workOrders = workOrders;
	}
	public String getScheduleDate() {
		return scheduleDate;
	}
	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}
	public String getCrewAssigned() {
		return crewAssigned;
	}
	public void setCrewAssigned(String crewAssigned) {
		this.crewAssigned = crewAssigned;
	}
	public String getLeadCrew() {
		return leadCrew;
	}
	public void setLeadCrew(String leadCrew) {
		this.leadCrew = leadCrew;
	}
	public String getSchedulingComment() {
		return schedulingComment;
	}
	public void setSchedulingComment(String schedulingComment) {
		this.schedulingComment = schedulingComment;
	}
	public String getDeportComment() {
		return deportComment;
	}
	public void setDeportComment(String deportComment) {
		this.deportComment = deportComment;
	}
	public List<ToDoAssignment> getTodoAssignments() {
		return todoAssignments;
	}
	public void setTodoAssignments(List<ToDoAssignment> todoAssignments) {
		this.todoAssignments = todoAssignments;
	}
	
	
	
}
