package au.com.wp.corp.p6.dto;

import java.util.List;

public class ExecutionPackageDTO {
	private String exctnPckgNam;
	private String leadCrew;
	private String crewNames;
	private List<ToDoItem> toDoItems;
	private List<String> workOrders;
	private String scheduleDate;
	public String getExctnPckgNam() {
		return exctnPckgNam;
	}
	public void setExctnPckgNam(String exctnPckgNam) {
		this.exctnPckgNam = exctnPckgNam;
	}
	public String getLeadCrew() {
		return leadCrew;
	}
	public void setLeadCrew(String leadCrew) {
		this.leadCrew = leadCrew;
	}
	public String getCrewNames() {
		return crewNames;
	}
	public void setCrewNames(String crewNames) {
		this.crewNames = crewNames;
	}
	public List<ToDoItem> getToDoItems() {
		return toDoItems;
	}
	public void setToDoItems(List<ToDoItem> toDoItems) {
		this.toDoItems = toDoItems;
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

}
