package au.com.wp.corp.p6.dto;

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
	private String todoName;
	private List<String> workOrders;
		
	
	public String getTmpltId() {
		return tmpltId;
	}
	public void setTmpltId(String tmpltId) {
		this.tmpltId = tmpltId;
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
	public String getTodoName() {
		return todoName;
	}
	public void setTodoName(String todoName) {
		this.todoName = todoName;
	}
	public List<String> getWorkOrders() {
		return workOrders;
	}
	public void setWorkOrders(List<String> workOrders) {
		this.workOrders = workOrders;
	}
	
}
