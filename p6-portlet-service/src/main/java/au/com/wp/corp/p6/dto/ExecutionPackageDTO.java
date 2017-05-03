package au.com.wp.corp.p6.dto;

import java.util.List;

public class ExecutionPackageDTO {
	private String exctnPckgName;
	private String leadCrew;
	private String crewNames;
	private List<WorkOrder> workOrders;
	private String actioned;
	
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
	/**
	 * @return the workOrders
	 */
	public List<WorkOrder> getWorkOrders() {
		return workOrders;
	}
	/**
	 * @param workOrders the workOrders to set
	 */
	public void setWorkOrders(List<WorkOrder> workOrders) {
		this.workOrders = workOrders;
	}
	public String getActioned() {
		return actioned;
	}
	public void setActioned(String actioned) {
		this.actioned = actioned;
	}
	
	

}
