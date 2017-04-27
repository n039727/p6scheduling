/**
 * 
 */
package au.com.wp.corp.p6.dto;

/**
 * @author N039603
 *
 */
public class EPCreateDTO {
	
	private String workOrderId;
	private String scheduleDate;
	private String crewAssigned;
	
	public String getWorkOrderId() {
		return workOrderId;
	}
	public void setWorkOrderId(String workOrderId) {
		this.workOrderId = workOrderId;
	}
	public String getScheduleDate() {
		return scheduleDate;
	}
	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}
	public String getCrewAssigned() {
		return crewAssigned;
	}
	public void setCrewAssigned(String crewAssigned) {
		this.crewAssigned = crewAssigned;
	}
	
	
	

}
