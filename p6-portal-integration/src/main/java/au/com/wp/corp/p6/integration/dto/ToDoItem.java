package au.com.wp.corp.p6.integration.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ToDoItem {
	
	
	private String tmpltId;
	private String crtdTs;
	private String crtdUsr;
	private String lstUpdtdTs;
	private String lstUpdtdUsr;
	private String tmpltDesc;
	private String todoId;
	private String toDoName;
	private List<String> workOrders;
	private String comments;	
	private Date reqdByDate ;
	private String status;
	private String supportingDocLink;
	private long typeId;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSupportingDocLink() {
		return supportingDocLink;
	}
	public void setSupportingDocLink(String supportingDocLink) {
		this.supportingDocLink = supportingDocLink;
	}
	public Date getReqdByDate() {
		return reqdByDate;
	}
	public void setReqdByDate(Date reqdByDate) {
		this.reqdByDate = reqdByDate;
	}
	public String getCrtdTs() {
		return crtdTs;
	}
	public void setCrtdTs(String crtdTs) {
		this.crtdTs = crtdTs;
	}
	public String getCrtdUsr() {
		return crtdUsr;
	}
	public void setCrtdUsr(String crtdUsr) {
		this.crtdUsr = crtdUsr;
	}
	public String getLstUpdtdTs() {
		return lstUpdtdTs;
	}
	public void setLstUpdtdTs(String lstUpdtdTs) {
		this.lstUpdtdTs = lstUpdtdTs;
	}
	public String getLstUpdtdUsr() {
		return lstUpdtdUsr;
	}
	public void setLstUpdtdUsr(String lstUpdtdUsr) {
		this.lstUpdtdUsr = lstUpdtdUsr;
	}
	public String getTmpltDesc() {
		return tmpltDesc;
	}
	public void setTmpltDesc(String tmpltDesc) {
		this.tmpltDesc = tmpltDesc;
	}
	public String getTodoId() {
		return todoId;
	}
	public void setTodoId(String todoId) {
		this.todoId = todoId;
	}
	public String getToDoName() {
		return toDoName;
	}
	public void setToDoName(String todoName) {
		this.toDoName = todoName;
	}
	public List<String> getWorkOrders() {
		return workOrders;
	}
	public void setWorkOrders(List<String> workOrders) {
		this.workOrders = workOrders;
	}

	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getTmpltId() {
		return tmpltId;
	}
	public void setTmpltId(String tmpltId) {
		this.tmpltId = tmpltId;
	}
	/**
	 * @return the typeId
	 */
	public long getTypeId() {
		return typeId;
	}
	/**
	 * @param typeId the typeId to set
	 */
	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}
}
