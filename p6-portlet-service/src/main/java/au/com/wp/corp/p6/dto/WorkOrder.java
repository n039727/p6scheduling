package au.com.wp.corp.p6.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class WorkOrder {
	
	private String executionPackage;
	private List<String> workOrders;
	private String scheduleDate;
	private String leadCrew;
	private List<ToDoItems> toDoItems;
	
	public String getExecutionPackage() {
		return executionPackage;
	}
	public void setExecutionPackage(String executionPackage) {
		this.executionPackage = executionPackage;
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
	public String getLeadCrew() {
		return leadCrew;
	}
	public void setLeadCrew(String leadCrew) {
		this.leadCrew = leadCrew;
	}
	public List<ToDoItems> getToDoItems() {
		return toDoItems;
	}
	public void setToDoItems(List<ToDoItems> toDoItems) {
		this.toDoItems = toDoItems;
	}
	
	
	

}
