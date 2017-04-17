/**
 * 
 */
package au.com.wp.corp.p6.dto;

/**
 * @author N039603
 *
 */
public class ToDoAssignment {

	private String toDoName;
	private String reqByDate;
	private String comment;
	private String status;
	private String supportingDoc;
	public String getToDoName() {
		return toDoName;
	}
	public void setToDoName(String toDoName) {
		this.toDoName = toDoName;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSupportingDoc() {
		return supportingDoc;
	}
	public void setSupportingDoc(String supportingDoc) {
		this.supportingDoc = supportingDoc;
	}
	
}
