/**
 * 
 */

package au.com.wp.corp.p6.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.wp.corp.p6.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.dto.P6ActivityDTO;
import au.com.wp.corp.p6.dto.P6ProjWorkgroupDTO;

/**
 * @author n039126
 *
 */
public class CacheManager {
	

	private static final Map<String, List<String>> wsHeaders = new HashMap<>();
	
	private CacheManager(){
		
	}
	
	public static final Map<String, List<String>> getWsHeaders () {
		return wsHeaders;
	}
	
	private final static Map<String, EllipseActivityDTO> ellipseActivities = new HashMap<>();

	public static final Map<String, EllipseActivityDTO> getEllipseActivitiesMap() {
		return ellipseActivities;
	}

	private final static Map<String, P6ActivityDTO> p6Activities = new HashMap<>();

	public static final Map<String, P6ActivityDTO> getP6ActivitiesMap() {
		return p6Activities;
	}
	
	private static final Map<String, ReadProcessStatus> systemReadStatus = new HashMap<>();
	
	public static final Map<String, ReadProcessStatus> getSystemReadStatusMap() {
		return systemReadStatus;
	}
	
	private final static Map<String, P6ProjWorkgroupDTO > p6ProjectWorkgroupMapping = new HashMap<>();

	public static final Map<String, P6ProjWorkgroupDTO> getP6ProjectWorkgroupMap() {
		return p6ProjectWorkgroupMapping;
	}
	
	private final static Map<String, List<String> > p6ProjectWorkgroupListMapp = new HashMap<>();

	public static final Map<String, List<String>> getProjectWorkgroupListMap() {
		return p6ProjectWorkgroupListMapp;
	}
	
}
