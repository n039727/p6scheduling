package au.com.wp.corp.p6.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class MaterialRequisitionRequest{
	private List<String> workOrderList;
	/**
	 * @return the workOrderList
	 */
	public List<String> getWorkOrderList() {
		return workOrderList;
	}
	/**
	 * @param workOrderList the workOrderList to set
	 */
	public void setWorkOrderList(List<String> workOrderList) {
		this.workOrderList = workOrderList;
	}
	
}