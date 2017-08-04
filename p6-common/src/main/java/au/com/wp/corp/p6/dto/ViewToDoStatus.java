/**
 * 
 */
package au.com.wp.corp.p6.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author N039603
 *
 */
@JsonInclude(Include.NON_NULL)
public class ViewToDoStatus {

	private String exctnPckgName;
	private List<String> workOrders;
	private String scheduleDate;
	private List<String> crewAssigned;
	private String leadCrew;
	private String schedulingComment;
	private String deportComment;
	private List<ToDoAssignment> todoAssignments;
	public String getExctnPckgName() {
		return exctnPckgName;
	}
	public void setExctnPckgName(String exctnPckgName) {
		this.exctnPckgName = exctnPckgName;
	}
	
	public List<String> getWorkOrders() {
		return workOrders;
	}
	public void setWorkOrders(List<String> workOrders) {
		this.workOrders = workOrders;
	}
	public String getScheduleDate() {
		return scheduleDate;
	}
	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}
	public List<String> getCrewAssigned() {
		return crewAssigned;
	}
	public void setCrewAssigned(List<String> crewAssigned) {
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
	@Override
	public String toString() {
		return "ViewToDoStatus [executionPackage=" + exctnPckgName + ", workOrders=" + workOrders + ", scheduleDate="
				+ scheduleDate + ", crewAssigned=" + crewAssigned + ", leadCrew=" + leadCrew + ", schedulingComment="
				+ schedulingComment + ", deportComment=" + deportComment + ", todoAssignments=" + todoAssignments + "]";
	}
	
	
	
}
