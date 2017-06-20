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
public class P6ActivityDTO implements Serializable {
	/**
	 * serial version id
	 */
	private static final long serialVersionUID = -2859048188743315099L;
	/**
	 * Primary Id in P6
	 */
	private Integer activityObjectId;
	/**
	 * activityId = workOrderId + taskId
	 */
	private String activityId;

	private String activityName;

	private String activityStatus;

	private String plannedStartDate;

	private String activityJDCodeUDF;

	private String taskUserStatusUDF;

	private String equipmentNoUDF;

	private String pickIdUDF;

	private String eGIUDF;

	private String equipmentCodeUDF;

	private double originalDuration;

	private double remainingDuration;

	private String requiredByDateUDF;

	private String addressUDF;

	private String locationInStreetUDF;

	private String taskDescriptionUDF;

	private String slippageCodeUDF;

	private String feederUDF;

	private String ellipseStandardJobUDF;

	private String upStreamSwitchUDF;

	/**
	 * Default value -1 is required as the double variable default value is 0.0
	 * but estimated labor hours can be 0.0
	 */
	private double estimatedLabourHours = -1;

	private int estimatedLabourHoursObjectId;

	private String workGroup;

	private int projectObjectId;

	private int primaryResorceObjectId;

	private String executionPckgUDF;

	private String actualStartDate;

	private String actualFinishDate;

	/**
	 * @return the estimatedLabourHours
	 */
	public double getEstimatedLabourHours() {
		return estimatedLabourHours;
	}

	/**
	 * @param estimatedLabourHours
	 *            the estimatedLabourHours to set
	 */
	public void setEstimatedLabourHours(double estimatedLabourHours) {
		this.estimatedLabourHours = estimatedLabourHours;
	}

	/**
	 * @return the estimatedLabourHoursObjectId
	 */
	public int getEstimatedLabourHoursObjectId() {
		return estimatedLabourHoursObjectId;
	}

	/**
	 * @param estimatedLabourHoursObjectId
	 *            the estimatedLabourHoursObjectId to set
	 */
	public void setEstimatedLabourHoursObjectId(int estimatedLabourHoursObjectId) {
		this.estimatedLabourHoursObjectId = estimatedLabourHoursObjectId;
	}

	/**
	 * @return the primaryResorceObjectId
	 */
	public int getPrimaryResorceObjectId() {
		return primaryResorceObjectId;
	}

	/**
	 * @param primaryResorceObjectId
	 *            the primaryResorceObjectId to set
	 */
	public void setPrimaryResorceObjectId(int primaryResorceObjectId) {
		this.primaryResorceObjectId = primaryResorceObjectId;
	}

	/**
	 * @return the activityObjectId
	 */
	public Integer getActivityObjectId() {
		return activityObjectId;
	}

	/**
	 * @param activityObjectId
	 *            the activityObjectId to set
	 */
	public void setActivityObjectId(Integer activityObjectId) {
		this.activityObjectId = activityObjectId;
	}

	/**
	 * @return the activityId
	 */
	public String getActivityId() {
		return activityId;
	}

	/**
	 * @param activityId
	 *            the activityId to set
	 */
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	/**
	 * @return the activityName
	 */
	public String getActivityName() {
		return activityName;
	}

	/**
	 * @param activityName
	 *            the activityName to set
	 */
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	/**
	 * @return the activityStatus
	 */
	public String getActivityStatus() {
		return activityStatus;
	}

	/**
	 * @param activityStatus
	 *            the activityStatus to set
	 */
	public void setActivityStatus(String activityStatus) {
		this.activityStatus = activityStatus;
	}

	/**
	 * @return the activityJDCodeUDF
	 */
	public String getActivityJDCodeUDF() {
		return activityJDCodeUDF;
	}

	/**
	 * @param activityJDCodeUDF
	 *            the activityJDCodeUDF to set
	 */
	public void setActivityJDCodeUDF(String activityJDCodeUDF) {
		this.activityJDCodeUDF = activityJDCodeUDF;
	}

	/**
	 * @return the taskUserStatusUDF
	 */
	public String getTaskUserStatusUDF() {
		return taskUserStatusUDF;
	}

	/**
	 * @param taskUserStatusUDF
	 *            the taskUserStatusUDF to set
	 */
	public void setTaskUserStatusUDF(String taskUserStatusUDF) {
		this.taskUserStatusUDF = taskUserStatusUDF;
	}

	/**
	 * @return the equipmentNoUDF
	 */
	public String getEquipmentNoUDF() {
		return equipmentNoUDF;
	}

	/**
	 * @param equipmentNoUDF
	 *            the equipmentNoUDF to set
	 */
	public void setEquipmentNoUDF(String equipmentNoUDF) {
		this.equipmentNoUDF = equipmentNoUDF;
	}

	/**
	 * @return the pickIdUDF
	 */
	public String getPickIdUDF() {
		return pickIdUDF;
	}

	/**
	 * @param pickIdUDF
	 *            the pickIdUDF to set
	 */
	public void setPickIdUDF(String pickIdUDF) {
		this.pickIdUDF = pickIdUDF;
	}

	/**
	 * @return the eGIUDF
	 */
	public String geteGIUDF() {
		return eGIUDF;
	}

	/**
	 * @param eGIUDF
	 *            the eGIUDF to set
	 */
	public void seteGIUDF(String eGIUDF) {
		this.eGIUDF = eGIUDF;
	}

	/**
	 * @return the equipmentCodeUDF
	 */
	public String getEquipmentCodeUDF() {
		return equipmentCodeUDF;
	}

	/**
	 * @param equipmentCodeUDF
	 *            the equipmentCodeUDF to set
	 */
	public void setEquipmentCodeUDF(String equipmentCodeUDF) {
		this.equipmentCodeUDF = equipmentCodeUDF;
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
	 * @return the plannedStartDate
	 */
	public String getPlannedStartDate() {
		return plannedStartDate;
	}

	/**
	 * @param plannedStartDate
	 *            the plannedStartDate to set
	 */
	public void setPlannedStartDate(String plannedStartDate) {
		this.plannedStartDate = plannedStartDate;
	}

	/**
	 * @return the requiredByDateUDF
	 */
	public String getRequiredByDateUDF() {
		return requiredByDateUDF;
	}

	/**
	 * @param requiredByDateUDF
	 *            the requiredByDateUDF to set
	 */
	public void setRequiredByDateUDF(String requiredByDateUDF) {
		this.requiredByDateUDF = requiredByDateUDF;
	}

	/**
	 * @return the feederUDF
	 */
	public String getFeederUDF() {
		return feederUDF;
	}

	/**
	 * @param feederUDF
	 *            the feederUDF to set
	 */
	public void setFeederUDF(String feederUDF) {
		this.feederUDF = feederUDF;
	}

	/**
	 * @return the ellipseStandardJobUDF
	 */
	public String getEllipseStandardJobUDF() {
		return ellipseStandardJobUDF;
	}

	/**
	 * @param ellipseStandardJobUDF
	 *            the ellipseStandardJobUDF to set
	 */
	public void setEllipseStandardJobUDF(String ellipseStandardJobUDF) {
		this.ellipseStandardJobUDF = ellipseStandardJobUDF;
	}

	/**
	 * @return the upStreamSwitchUDF
	 */
	public String getUpStreamSwitchUDF() {
		return upStreamSwitchUDF;
	}

	/**
	 * @param upStreamSwitchUDF
	 *            the upStreamSwitchUDF to set
	 */
	public void setUpStreamSwitchUDF(String upStreamSwitchUDF) {
		this.upStreamSwitchUDF = upStreamSwitchUDF;
	}

	/**
	 * @return the workGroup
	 */
	public String getWorkGroup() {
		return workGroup;
	}

	/**
	 * @param workGroup
	 *            the workGroup to set
	 */
	public void setWorkGroup(String workGroup) {
		this.workGroup = workGroup;
	}

	/**
	 * @return the addressUDF
	 */
	public String getAddressUDF() {
		return addressUDF;
	}

	/**
	 * @param addressUDF
	 *            the addressUDF to set
	 */
	public void setAddressUDF(String addressUDF) {
		this.addressUDF = addressUDF;
	}

	/**
	 * @return the locationInStreetUDF
	 */
	public String getLocationInStreetUDF() {
		return locationInStreetUDF;
	}

	/**
	 * @param locationInStreetUDF
	 *            the locationInStreetUDF to set
	 */
	public void setLocationInStreetUDF(String locationInStreetUDF) {
		this.locationInStreetUDF = locationInStreetUDF;
	}

	/**
	 * @return the taskDescriptionUDF
	 */
	public String getTaskDescriptionUDF() {
		return taskDescriptionUDF;
	}

	/**
	 * @param taskDescriptionUDF
	 *            the taskDescriptionUDF to set
	 */
	public void setTaskDescriptionUDF(String taskDescriptionUDF) {
		this.taskDescriptionUDF = taskDescriptionUDF;
	}

	/**
	 * @return the slippageCodeUDF
	 */
	public String getSlippageCodeUDF() {
		return slippageCodeUDF;
	}

	/**
	 * @param slippageCodeUDF
	 *            the slippageCodeUDF to set
	 */
	public void setSlippageCodeUDF(String slippageCodeUDF) {
		this.slippageCodeUDF = slippageCodeUDF;
	}

	/**
	 * @return the projectId
	 */
	public int getProjectObjectId() {
		return projectObjectId;
	}

	/**
	 * @param projectId
	 *            the projectId to set
	 */
	public void setProjectObjectId(int projectObjectId) {
		this.projectObjectId = projectObjectId;
	}

	/**
	 * @return the executionPckgUDF
	 */
	public String getExecutionPckgUDF() {
		return executionPckgUDF;
	}

	/**
	 * @param executionPckgUDF
	 *            the executionPckgUDF to set
	 */
	public void setExecutionPckgUDF(String executionPckgUDF) {
		this.executionPckgUDF = executionPckgUDF;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityId == null) ? 0 : activityId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof P6ActivityDTO)) {
			return false;
		}
		P6ActivityDTO other = (P6ActivityDTO) obj;
		if (activityId == null) {
			if (other.activityId != null) {
				return false;
			}
		} else if (!activityId.equals(other.activityId)) {
			return false;
		}
		return true;
	}

}
