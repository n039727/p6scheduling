/**
 * 
 */

package au.com.wp.corp.p6.integration.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
import au.com.wp.corp.p6.integration.dto.UDFTypeDTO;
import au.com.wp.corp.p6.integration.dto.WorkOrder;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;

/**
 * @author n039126
 * @version 1.0
 */
public class CacheManager {
	

	private static final Map<String, List<String>> wsHeaders = new HashMap<>();
	
	
	private static final Map<String, P6ActivityDTO> p6Activities = new HashMap<>();
	
	private static final Map<String, ReadWriteProcessStatus> systemReadWriteStatus = new HashMap<>();

	
	private static final Map<String, List<String> > p6ProjectWorkgroupListMapp = new HashMap<>();

	private static final Map<String, UDFTypeDTO> p6UDFTypes = new HashMap<>();
	
	private static  final Map<String, List<Integer>> udfValueMap = new HashMap<>();
	
	private static final Map<String, Integer> projectsMap = new HashMap<>();


	private static List<Exception> dataErrors = null;
	
	private CacheManager(){
		
	}
	private static final Set<Task> tasksForUpdate = new HashSet<Task>(); 
	private static final Set<Task> tasksForRemove = new HashSet<Task>();
	private static final Set<String> deletetedExecPkagList = new HashSet<String>(); 
	private static final Set<ExecutionPackage> execPkgListForUpdate = new HashSet<ExecutionPackage>(); 
	private static final Set<WorkOrder> executionPackageNameForUpdate = new HashSet<WorkOrder>();
	private static final Set<WorkOrder> executionPackageForCreate = new HashSet<WorkOrder>();

	
	public static Set<WorkOrder> getExecutionpackageforcreate() {
		return executionPackageForCreate;
	}

	public static Set<WorkOrder> getExecutionpackagenameforupdate() {
		return executionPackageNameForUpdate;
	}

	public static Set<Task> getTasksforremove() {
		return tasksForRemove;
	}

	public static Set<Task> getTasksforupdate() {
		return tasksForUpdate;
	}

	public static Set<String> getDeletetedexecpkaglist() {
		return deletetedExecPkagList;
	}

	public static Set<ExecutionPackage> getExecpkglistforupdate() {
		return execPkgListForUpdate;
	}
	public static final List<Exception> getDataErrors()
	{
		if ( null == dataErrors)
			dataErrors = new ArrayList<>();
		return dataErrors;
	}
	public static final Map<String, Integer> getProjectsMap () {
		return projectsMap;
	}
	
	public static final Map<String, List<Integer>> getUDFValueMap () {
		return udfValueMap;
	}
	
	
	public static final Map<String, List<String>> getWsHeaders () {
		return wsHeaders;
	}
	
	
	public static final Map<String, P6ActivityDTO> getP6ActivitiesMap() {
		return p6Activities;
	}
	
	
	public static final Map<String, ReadWriteProcessStatus> getSystemReadWriteStatusMap() {
		return systemReadWriteStatus;
	}
	

	public static final Map<String, List<String>> getProjectWorkgroupListMap() {
		return p6ProjectWorkgroupListMapp;
	}
	

	public static final Map<String, UDFTypeDTO> getP6UDFTypeMap() {
		return p6UDFTypes;
	}
	
	
}
