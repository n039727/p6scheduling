/**
 * 
 */
package au.com.wp.corp.p6.integration.dto;

import java.io.Serializable;

/**
 * ActivityDTO holds the activity details
 * 
 * @author N039126
 * @version 1.0
 */
public class EllipseActivityDTO implements Serializable {
	/**
	 * serial id
	 */
	private static final long serialVersionUID = 5740767208457113061L;

	/**
	 * activityId = workOrderId + taskId
	 */
	private String workOrderTaskId;

	private String workGroup;

	private String workOrderDescription;

	private String taskStatus;

	private String plannedStartDate;

	private String plannedFinishDate;

	private String jdCode;

	private String taskUserStatus;

	private String equipmentNo;

	private String plantNoOrPickId;

	private String EGI;

	private String equipmentCode;

	private double originalDuration;

	private double remainingDuration;

	private String requiredByDate;

	private String ellipseStandardJob;

	private String feeder;

	private String upStreamSwitch;

	private String estimatedLabourHours;

	private String address;

	private String locationInStreet;

	private String taskDescription;

	private String slippageCode;

	private String actualStartDate;

	private String actualFinishDate;
	
	private String calcDurFlag;

	/**
	 * @return the workOrderTaskId
	 */
	public String getWorkOrderTaskId() {
		return workOrderTaskId != null ? workOrderTaskId.trim() : "";
	}

	/**
	 * @param workOrderTaskId
	 *            the workOrderTaskId to set
	 */
	public void setWorkOrderTaskId(String workOrderTaskId) {
		this.workOrderTaskId = workOrderTaskId;
	}

	/**
	 * @return the workGroup
	 */
	public String getWorkGroup() {
		return workGroup != null ? workGroup.trim() : "";
	}

	/**
	 * @param workGroup
	 *            the workGroup to set
	 */
	public void setWorkGroup(String workGroup) {
		this.workGroup = workGroup;
	}

	/**
	 * @return the workOrderDescription
	 */
	public String getWorkOrderDescription() {
		return workOrderDescription != null ? workOrderDescription.trim() : "";
	}

	/**
	 * @param workOrderDescription
	 *            the workOrderDescription to set
	 */
	public void setWorkOrderDescription(String workOrderDescription) {
		this.workOrderDescription = workOrderDescription;
	}

	/**
	 * @return the taskStatus
	 */
	public String getTaskStatus() {
		return taskStatus != null ? taskStatus.trim() : "";
	}

	/**
	 * @param taskStatus
	 *            the taskStatus to set
	 */
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	/**
	 * @return the plannedStartDate
	 */
	public String getPlannedStartDate() {
		return plannedStartDate != null ? plannedStartDate.trim() : "";
	}

	/**
	 * @param plannedStartDate
	 *            the plannedStartDate to set
	 */
	public void setPlannedStartDate(String plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	/**
	 * @return the jdCode
	 */
	public String getJdCode() {
		return jdCode != null ? jdCode.trim() : "";
	}

	/**
	 * @param jdCode
	 *            the jdCode to set
	 */
	public void setJdCode(String jdCode) {
		this.jdCode = jdCode;
	}

	/**
	 * @return the taskUserStatus
	 */
	public String getTaskUserStatus() {
		return taskUserStatus != null ? taskUserStatus.trim() : "";
	}

	/**
	 * @param taskUserStatus
	 *            the taskUserStatus to set
	 */
	public void setTaskUserStatus(String taskUserStatus) {
		this.taskUserStatus = taskUserStatus;
	}

	/**
	 * @return the equipmentNo
	 */
	public String getEquipmentNo() {
		return equipmentNo != null ? equipmentNo.trim() : "";
	}

	/**
	 * @param equipmentNo
	 *            the equipmentNo to set
	 */
	public void setEquipmentNo(String equipmentNo) {
		this.equipmentNo = equipmentNo;
	}

	/**
	 * @return the plantNoOrPickId
	 */
	public String getPlantNoOrPickId() {
		return plantNoOrPickId != null ? plantNoOrPickId.trim() : "";
	}

	/**
	 * @param plantNoOrPickId
	 *            the plantNoOrPickId to set
	 */
	public void setPlantNoOrPickId(String plantNoOrPickId) {
		this.plantNoOrPickId = plantNoOrPickId;
	}

	/**
	 * @return the eGI
	 */
	public String getEGI() {
		return EGI != null ? EGI.trim() : "";
	}

	/**
	 * @param eGI
	 *            the eGI to set
	 */
	public void setEGI(String eGI) {
		this.EGI = eGI;
	}

	/**
	 * @return the equipmentCode
	 */
	public String getEquipmentCode() {
		return equipmentCode != null ? equipmentCode.trim() : "";
	}

	/**
	 * @param equipmentCode
	 *            the equipmentCode to set
	 */
	public void setEquipmentCode(String equipmentCode) {
		this.equipmentCode = equipmentCode;
	}

	/**
	 * @return the originalDuration
	 */
	public double getOriginalDuration() {
		return originalDuration;
	}

	/**
	 * @param originalDuration
	 *            the originalDuration to set
	 */
	public void setOriginalDuration(double originalDuration) {
		this.originalDuration = originalDuration;
	}

	/**
	 * @return the remainingDuration
	 */
	public double getRemainingDuration() {
		return remainingDuration;
	}

	/**
	 * @param remainingDuration
	 *            the remainingDuration to set
	 */
	public void setRemainingDuration(double remainingDuration) {
		this.remainingDuration = remainingDuration;
	}

	/**
	 * @return the requiredByDate
	 */
	public String getRequiredByDate() {
		return requiredByDate != null ? requiredByDate.trim() : "";
	}

	/**
	 * @param requiredByDate
	 *            the requiredByDate to set
	 */
	public void setRequiredByDate(String requiredByDate) {
		this.requiredByDate = requiredByDate;
	}

	/**
	 * @return the ellipseStandardJob
	 */
	public String getEllipseStandardJob() {
		return ellipseStandardJob != null ? ellipseStandardJob.trim() : "";
	}

	/**
	 * @param ellipseStandardJob
	 *            the ellipseStandardJob to set
	 */
	public void setEllipseStandardJob(String ellipseStandardJob) {
		this.ellipseStandardJob = ellipseStandardJob;
	}

	/**
	 * @return the feeder
	 */
	public String getFeeder() {
		return feeder != null ? feeder.trim() : "";
	}

	/**
	 * @param feeder
	 *            the feeder to set
	 */
	public void setFeeder(String feeder) {
		this.feeder = feeder;
	}

	/**
	 * @return the upStreamSwitch
	 */
	public String getUpStreamSwitch() {
		return upStreamSwitch != null ? upStreamSwitch.trim() : "";
	}

	/**
	 * @param upStreamSwitch
	 *            the upStreamSwitch to set
	 */
	public void setUpStreamSwitch(String upStreamSwitch) {
		this.upStreamSwitch = upStreamSwitch;
	}

	/**
	 * @return the estimatedLabourHours
	 */
	public String getEstimatedLabourHours() {
		return estimatedLabourHours != null ? estimatedLabourHours.trim() : "";
	}

	/**
	 * @param estimatedLabourHours
	 *            the estimatedLabourHours to set
	 */
	public void setEstimatedLabourHours(String estimatedLabourHours) {
		this.estimatedLabourHours = estimatedLabourHours;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address != null ? address.trim() : "";
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the locationInStreet
	 */
	public String getLocationInStreet() {
		return locationInStreet != null ? locationInStreet.trim() : "";
	}

	/**
	 * @param locationInStreet
	 *            the locationInStreet to set
	 */
	public void setLocationInStreet(String locationInStreet) {
		this.locationInStreet = locationInStreet;
	}

	/**
	 * @return the taskDescription
	 */
	public String getTaskDescription() {
		return taskDescription != null ? taskDescription.trim() : "";
	}

	/**
	 * @param taskDescription
	 *            the taskDescription to set
	 */
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}

	/**
	 * @return the slippageCode
	 */
	public String getSlippageCode() {
		return slippageCode != null ? slippageCode.trim() : "";
	}

	/**
	 * @param slippageCode
	 *            the slippageCode to set
	 */
	public void setSlippageCode(String slippageCode) {
		this.slippageCode = slippageCode;
	}

	/**
	 * @return the actualStartDate
	 */
	public String getActualStartDate() {
		return actualStartDate;
	}

	/**
	 * @param actualStartDate
	 *            the actualStartDate to set
	 */
	public void setActualStartDate(String actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	/**
	 * @return the actualFinishDate
	 */
	public String getActualFinishDate() {
		return actualFinishDate;
	}

	/**
	 * @param actualFinishDate
	 *            the actualFinishDate to set
	 */
	public void setActualFinishDate(String actualFinishDate) {
		this.actualFinishDate = actualFinishDate;
	}

	/**
	 * @return the plannedFinishDate
	 */
	public String getPlannedFinishDate() {
		return plannedFinishDate;
	}

	/**
	 * @param plannedFinishDate
	 *            the plannedFinishDate to set
	 */
	public void setPlannedFinishDate(String plannedFinishDate) {
		this.plannedFinishDate = plannedFinishDate;
	}

		
	
	/**
	 * @return the calcDurFlag
	 */
	public String getCalcDurFlag() {
		return calcDurFlag;
	}

	/**
	 * @param calcDurFlag the calcDurFlag to set
	 */
	public void setCalcDurFlag(String calcDurFlag) {
		this.calcDurFlag = calcDurFlag;
	}

	@Override
	public int hashCode() {
		return workOrderTaskId.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EllipseActivityDTO) {
			EllipseActivityDTO other = (EllipseActivityDTO) obj;
			if (this.getWorkOrderTaskId().equals(other.getWorkOrderTaskId())) {
				return true;
			}
		}
		return false;
	}
}
