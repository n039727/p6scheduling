/**
 * 
 */
package au.com.wp.corp.p6.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author N039603
 *
 */
@JsonInclude(Include.NON_NULL)
public class ResourceDTO {
	
	private volatile Map<String, List<String>> depotCrewMap = null;
	private String grpName;
	
	/**
	 * @return the grpName
	 */
	public String getGrpName() {
		return grpName;
	}
	/**
	 * @param grpName the grpName to set
	 */
	public void setGrpName(String grpName) {
		this.grpName = grpName;
	}
	/**
	 * @return the depotName
	 */
	/**
	 * @return the depotCrewMap
	 */
	public Map<String, List<String>> getDepotCrewMap() {
		return depotCrewMap;
	}
	/**
	 * @param depotCrewMap the depotCrewMap to set
	 */
	public void setDepotCrewMap(Map<String, List<String>> depotCrewMap) {
		this.depotCrewMap = depotCrewMap;
	}


}
