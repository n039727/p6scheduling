package au.com.wp.corp.p6.dto;

import java.util.List;

public class ExecutionPackageDTO {
	private String exctnPckgNam;
	private String leadCrew;
	private String crewNames;
	private List<WorkOrder> workOrders;
	
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
	
	

}
