package au.com.wp.corp.p6.businessservice.dto;

import java.sql.Timestamp;
import java.util.Date;

public class TaskDTO {
	
	private String taskId;

	private String cmts;


	private String crewId;

	
	private Timestamp crtdTs;

	
	private String crtdUsr;

	
	private String depotId;

	
	private String leadCrewId;

	private Timestamp lstUpdtdTs;

	private String lstUpdtdUsr;

	private String matrlReqRef;

	private Date schdDt;

	private long executionPackageId;


	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getCmts() {
		return cmts;
	}

	public void setCmts(String cmts) {
		this.cmts = cmts;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public Timestamp getCrtdTs() {
		return crtdTs;
	}

	public void setCrtdTs(Timestamp crtdTs) {
		this.crtdTs = crtdTs;
	}

	public String getCrtdUsr() {
		return crtdUsr;
	}

	public void setCrtdUsr(String crtdUsr) {
		this.crtdUsr = crtdUsr;
	}

	public String getDepotId() {
		return depotId;
	}

	public void setDepotId(String depotId) {
		this.depotId = depotId;
	}

	public String getLeadCrewId() {
		return leadCrewId;
	}

	public void setLeadCrewId(String leadCrewId) {
		this.leadCrewId = leadCrewId;
	}

	public Timestamp getLstUpdtdTs() {
		return lstUpdtdTs;
	}

	public void setLstUpdtdTs(Timestamp lstUpdtdTs) {
		this.lstUpdtdTs = lstUpdtdTs;
	}

	public String getLstUpdtdUsr() {
		return lstUpdtdUsr;
	}

	public void setLstUpdtdUsr(String lstUpdtdUsr) {
		this.lstUpdtdUsr = lstUpdtdUsr;
	}

	public String getMatrlReqRef() {
		return matrlReqRef;
	}

	public void setMatrlReqRef(String matrlReqRef) {
		this.matrlReqRef = matrlReqRef;
	}

	public Date getSchdDt() {
		return schdDt;
	}

	public void setSchdDt(Date schdDt) {
		this.schdDt = schdDt;
	}

	public long getExecutionPackageId() {
		return executionPackageId;
	}

	public void setExecutionPackageId(long executionPackageId) {
		this.executionPackageId = executionPackageId;
	}

	
}
