package au.com.wp.corp.p6.businessservice;

import java.util.List;

public class WorkOrder {
	
	private String executionPackage;
	private List<String> workOrders;
	private String scheduleDate;
	private String leadCrew;
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
	
	
	

}
