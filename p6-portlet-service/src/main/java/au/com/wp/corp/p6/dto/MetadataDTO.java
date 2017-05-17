/**
 * 
 */
package au.com.wp.corp.p6.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author N039603
 *
 */
@JsonInclude(Include.NON_NULL)
public class MetadataDTO {
	
	private List<ToDoItem> toDoItems;
	private List<Crew> crews;
	/**
	 * @return the toDoItems
	 */
	public List<ToDoItem> getToDoItems() {
		return toDoItems;
	}
	/**
	 * @param toDoItems the toDoItems to set
	 */
	public void setToDoItems(List<ToDoItem> toDoItems) {
		this.toDoItems = toDoItems;
	}
	/**
	 * @return the crews
	 */
	public List<Crew> getCrews() {
		return crews;
	}
	/**
	 * @param crews the crews to set
	 */
	public void setCrews(List<Crew> crews) {
		this.crews = crews;
	}
	

}
