package au.com.wp.corp.p6.integration.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonInclude(Include.NON_NULL)
public class ExecutionPackageDTO {
	private String exctnPckgName;
	private String leadCrew;
	private String crewNames;
	private List<WorkOrder> workOrders;
	private String actioned;
	private String execSchdlrCmt;
	private String execDeptCmt;
	
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
	/**
	 * @return the execSchdlrCmt
	 */
	public String getExecSchdlrCmt() {
		return execSchdlrCmt;
	}
	/**
	 * @param execSchdlrCmt the execSchdlrCmt to set
	 */
	public void setExecSchdlrCmt(String execSchdlrCmt) {
		this.execSchdlrCmt = execSchdlrCmt;
	}
	/**
	 * @return the execDeptCmt
	 */
	public String getExecDeptCmt() {
		return execDeptCmt;
	}
	/**
	 * @param execDeptCmt the execDeptCmt to set
	 */
	public void setExecDeptCmt(String execDeptCmt) {
		this.execDeptCmt = execDeptCmt;
	}
	
	

}
