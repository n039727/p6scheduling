package au.com.wp.corp.p6.integration.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class WorkOrder {
	
	private String exctnPckgName;
	private List<String> workOrders;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<String> crewAssigned = new ArrayList();
	private String workOrderId;
	private String scheduleDate;
	private String leadCrew;
	private List<ToDoItem> toDoItems;
	private String schedulingToDoComment;
	private String depotToDoComment;
	private String crewNames;
	private String depotId;
	private String meterialReqRef;
	
	private String actioned;
	private String completed;
	private String executionPkgComment;
	private String executionPkgDepotComment;
	
	
	
	/**
	 * @return the executionPkgDepotComment
	 */
	public String getExecutionPkgDepotComment() {
		return executionPkgDepotComment;
	}
	/**
	 * @param executionPkgDepotComment the executionPkgDepotComment to set
	 */
	public void setExecutionPkgDepotComment(String executionPkgDepotComment) {
		this.executionPkgDepotComment = executionPkgDepotComment;
	}
	/**
	 * @return the workOrderId
	 */
	public String getWorkOrderId() {
		return workOrderId;
	}
	/**
	 * @param workOrderId the workOrderId to set
	 */
	public void setWorkOrderId(String workOrderId) {
		this.workOrderId = workOrderId;
	}
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
	
	/**
	 * @return the exctnPckgName
	 */
	public String getExctnPckgName() {
		return exctnPckgName;
	}
	/**
	 * @param exctnPckgName the exctnPckgName to set
	 */
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
	/**
	 * @return the actioned
	 */
	public String getActioned() {
		return actioned;
	}
	/**
	 * @param actioned the actioned to set
	 */
	public void setActioned(String actioned) {
		this.actioned = actioned;
	}
	public String getCompleted() {
		return completed;
	}
	public void setCompleted(String completed) {
		this.completed = completed;
	}
	/**
	 * @return the schedulerPkgComment
	 */
	public String getExecutionPkgComment() {
		return executionPkgComment;
	}
	/**
	 * @param schedulerPkgComment the schedulerPkgComment to set
	 */
	public void setExecutionPkgComment(String executionPkgComment) {
		this.executionPkgComment = executionPkgComment;
	}
	/**
	 * @return the crewAssigned
	 */
	public List<String> getCrewAssigned() {
		return crewAssigned;
	}
	/**
	 * @param crewAssigned the crewAssigned to set
	 */
	public void setCrewAssigned(List<String> crewAssigned) {
		this.crewAssigned = crewAssigned;
	}
	
	
}
