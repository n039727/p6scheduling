package au.com.wp.corp.p6.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class WorkOrderSearchInput {
	
	private List<String> depotList;
	private List<String> crewList;
	private String workOrderId;
	private String fromDate;
	private String toDate;
	private String execPckgName;
	
	/**
	 * @return the depotList
	 */
	public List<String> getDepotList() {
		return depotList;
	}
	/**
	 * @param depotList the depotList to set
	 */
	public void setDepotList(List<String> depotList) {
		this.depotList = depotList;
	}
	/**
	 * @return the crewList
	 */
	public List<String> getCrewList() {
		return crewList;
	}
	/**
	 * @param crewList the crewList to set
	 */
	public void setCrewList(List<String> crewList) {
		this.crewList = crewList;
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
	/**
	 * @return the fromDate
	 */
	public String getFromDate() {
		return fromDate;
	}
	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	/**
	 * @return the toDate
	 */
	public String getToDate() {
		return toDate;
	}
	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getExecPckgName() {
		return execPckgName;
	}
	public void setExecPckgName(String execPckgName) {
		this.execPckgName = execPckgName;
	}

	
}
