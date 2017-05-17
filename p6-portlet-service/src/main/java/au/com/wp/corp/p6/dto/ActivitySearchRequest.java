/**
 * 
 */
package au.com.wp.corp.p6.dto;

import java.util.List;

/**
 * Work order details search request object
 * @author n039126
 * @version 1.0
 */
public class ActivitySearchRequest {
	/**
	 *  List of crews
	 */
	private List<String> crewList;
	/**
	 * planned start date
	 */
	private String plannedStartDate;
	/**
	 * work order id
	 */
	private String workOrder;
	/**
	 * List of Depots
	 */
	private List<String> depotList;
	
	/**
	 * P6 filter criteria
	 */
	private String filter;
		
	
	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}
	/**
	 * @param filter the filter to set
	 */
	public void setFilter(String filter) {
		this.filter = filter;
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
	 * @return the plannedStartDate
	 */
	public String getPlannedStartDate() {
		return plannedStartDate;
	}
	/**
	 * @param plannedStartDate the plannedStartDate to set
	 */
	public void setPlannedStartDate(String plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}
	/**
	 * @return the workOrder
	 */
	public String getWorkOrder() {
		return workOrder;
	}
	/**
	 * @param workOrder the workOrder to set
	 */
	public void setWorkOrder(String workOrder) {
		this.workOrder = workOrder;
	}
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

	@Override
	public boolean equals (Object obj) {
		if ( obj instanceof ActivitySearchRequest) {
			ActivitySearchRequest sr = (ActivitySearchRequest)obj;
			return sr.getPlannedStartDate().equals(this.getPlannedStartDate());
		}
		
		return false;
	}
	
	@Override
	public int hashCode (){
		return 100;
	}
	
}
