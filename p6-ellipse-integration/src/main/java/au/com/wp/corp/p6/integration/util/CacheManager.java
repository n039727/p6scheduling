/**
 * 
 */

package au.com.wp.corp.p6.integration.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.wp.corp.p6.integration.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
import au.com.wp.corp.p6.integration.dto.P6ProjWorkgroupDTO;
import au.com.wp.corp.p6.integration.dto.UDFTypeDTO;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValue;

/**
 * @author n039126
 * @version 1.0
 */
public class CacheManager {
	

	private static final Map<String, List<String>> wsHeaders = new HashMap<>();
	
	private static final Map<String, EllipseActivityDTO> ellipseActivities = new HashMap<>();
	
	private static final Map<String, P6ActivityDTO> p6Activities = new HashMap<>();
	
	private static final Map<String, ReadWriteProcessStatus> systemReadWriteStatus = new HashMap<>();

	private static final Map<String, P6ProjWorkgroupDTO > p6ProjectWorkgroupMapping = new HashMap<>();
	
	private static final Map<String, List<String> > p6ProjectWorkgroupListMapp = new HashMap<>();

	private static final Map<String, UDFTypeDTO> p6UDFTypes = new HashMap<>();
	
	private static  final Map<Integer, List<UDFValue>> udfValueMap = new HashMap<>();
	
	private static final Map<String, Integer> projectsMap = new HashMap<>();

	
	private CacheManager(){
		
	}
	

	public static final Map<String, Integer> getProjectsMap () {
		return projectsMap;
	}
	
	public static final Map<Integer, List<UDFValue>> getUDFValueMap () {
		return udfValueMap;
	}
	
	
	public static final Map<String, List<String>> getWsHeaders () {
		return wsHeaders;
	}
	

	public static final Map<String, EllipseActivityDTO> getEllipseActivitiesMap() {
		return ellipseActivities;
	}

	
	public static final Map<String, P6ActivityDTO> getP6ActivitiesMap() {
		return p6Activities;
	}
	
	
	public static final Map<String, ReadWriteProcessStatus> getSystemReadWriteStatusMap() {
		return systemReadWriteStatus;
	}
	

	public static final Map<String, P6ProjWorkgroupDTO> getP6ProjectWorkgroupMap() {
		return p6ProjectWorkgroupMapping;
	}
	

	public static final Map<String, List<String>> getProjectWorkgroupListMap() {
		return p6ProjectWorkgroupListMapp;
	}
	

	public static final Map<String, UDFTypeDTO> getP6UDFTypeMap() {
		return p6UDFTypes;
	}
	
	
}
