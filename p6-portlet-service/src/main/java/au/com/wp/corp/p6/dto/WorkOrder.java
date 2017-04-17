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
	private List<ToDoItem> toDoItems;
	private String schedulingToDoComment;
	private String depotToDoComment;
	private String crewNames;
	private String depotId;
	private String meterialReqRef;
	
	public String getMeterialReqRef() {
		return meterialReqRef;
	}
	public void setMeterialReqRef(String meterialReqRef) {
		this.meterialReqRef = meterialReqRef;
	}
	public String getDepotId() {
		return depotId;
	}
	public void setDepotId(String depotId) {
		this.depotId = depotId;
	}
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
	public List<ToDoItem> getToDoItems() {
		return toDoItems;
	}
	public void setToDoItems(List<ToDoItem> toDoItems) {
		this.toDoItems = toDoItems;
	}
	public String getSchedulingToDoComment() {
		return schedulingToDoComment;
	}
	public void setSchedulingToDoComment(String schedulingToDoComment) {
		this.schedulingToDoComment = schedulingToDoComment;
	}
	public String getDepotToDoComment() {
		return depotToDoComment;
	}
	public void setDepotToDoComment(String depotToDoComment) {
		this.depotToDoComment = depotToDoComment;
	}
	public String getCrewNames() {
		return crewNames;
	}
	public void setCrewNames(String crewNames) {
		this.crewNames = crewNames;
	}
	
	
	

}
