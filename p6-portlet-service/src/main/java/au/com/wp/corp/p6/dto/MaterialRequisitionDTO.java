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
public class MaterialRequisitionDTO {
	
	private Map<String,List<String>> MaterialRequisitionMap;

	public Map<String, List<String>> getMaterialRequisitionMap() {
		return MaterialRequisitionMap;
	}

	public void setMaterialRequisitionMap(Map<String, List<String>> materialRequisitionMap) {
		MaterialRequisitionMap = materialRequisitionMap;
	}
	
 
}
