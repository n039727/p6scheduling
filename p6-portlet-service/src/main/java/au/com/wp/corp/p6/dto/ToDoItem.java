package au.com.wp.corp.p6.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ToDoItem {
	private String toDoName;
	private List<String> workOrders;
	private String reqByDate;
	private String comment;
	private String status;
	private String supportingDocLink;
	
	public String getToDoName() {
		return toDoName;
	}
	public void setToDoName(String toDoName) {
		this.toDoName = toDoName;
	}
	public List<String> getWorkOrders() {
		return workOrders;
	}
	public void setWorkOrders(List<String> workOrders) {
		this.workOrders = workOrders;
	}
	public String getReqByDate() {
		return reqByDate;
	}
	public void setReqByDate(String reqByDate) {
		this.reqByDate = reqByDate;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getSupportingDocLink() {
		return supportingDocLink;
	}
	public void setSupportingDocLink(String supportingDocLink) {
		this.supportingDocLink = supportingDocLink;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
