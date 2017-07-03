/**
 * 
 */

package au.com.wp.corp.p6.integration.util;

import java.util.ArrayList;
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

	private static Map<String, List<String>> wsHeaders = null;

	private static Map<String, EllipseActivityDTO> ellipseActivities = null;

	private static Map<String, P6ActivityDTO> p6Activities = null;

	private static Map<String, ReadWriteProcessStatus> systemReadWriteStatus = null;

	private static Map<String, P6ProjWorkgroupDTO> p6ProjectWorkgroupMapping = null;

	private static Map<String, List<String>> p6ProjectWorkgroupListMapp = null;

	private static Map<String, UDFTypeDTO> p6UDFTypes = null;

	private static Map<Integer, List<UDFValue>> udfValueMap = null;

	private static Map<String, Integer> projectsMap = null;
	
	private static List<Exception> dataErrors = null;
	
	

	private CacheManager() {

	}
	
	
	public static final List<Exception> getDataErrors()
	{
		if ( null == dataErrors)
			dataErrors = new ArrayList<>();
		return dataErrors;
	}

	public static final Map<String, Integer> getProjectsMap() {
		if (null == projectsMap)
			projectsMap = new HashMap<>();

		return projectsMap;
	}

	public static final Map<Integer, List<UDFValue>> getUDFValueMap() {
		if (null == udfValueMap)
			udfValueMap = new HashMap<>();
		return udfValueMap;
	}

	public static final Map<String, List<String>> getWsHeaders() {
		if (null == wsHeaders)
			wsHeaders = new HashMap<>();
		return wsHeaders;
	}

	public static final Map<String, EllipseActivityDTO> getEllipseActivitiesMap() {
		if (null == ellipseActivities)
			ellipseActivities = new HashMap<>();
		return ellipseActivities;
	}

	public static final Map<String, P6ActivityDTO> getP6ActivitiesMap() {
		if (null == p6Activities)
			p6Activities = new HashMap<>();
		return p6Activities;
	}

	public static final Map<String, ReadWriteProcessStatus> getSystemReadWriteStatusMap() {
		if (null == systemReadWriteStatus)
			systemReadWriteStatus = new HashMap<>();
		return systemReadWriteStatus;
	}

	public static final Map<String, P6ProjWorkgroupDTO> getP6ProjectWorkgroupMap() {
		if (null == p6ProjectWorkgroupMapping)
			p6ProjectWorkgroupMapping = new HashMap<>();
		return p6ProjectWorkgroupMapping;
	}

	public static final Map<String, List<String>> getProjectWorkgroupListMap() {
		if (null == p6ProjectWorkgroupListMapp)
			p6ProjectWorkgroupListMapp = new HashMap<>();
		return p6ProjectWorkgroupListMapp;
	}

	public static final Map<String, UDFTypeDTO> getP6UDFTypeMap() {
		if (null == p6UDFTypes)
			p6UDFTypes = new HashMap<>();
		return p6UDFTypes;
	}

	public static final void clear() {
		wsHeaders = null;
		ellipseActivities = null;
		p6Activities = null;

		systemReadWriteStatus = null;

		p6ProjectWorkgroupMapping = null;

		p6ProjectWorkgroupListMapp = null;

		p6UDFTypes = null;

		udfValueMap = null;

		projectsMap = null;
	}

}
