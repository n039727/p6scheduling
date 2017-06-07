/**
 * 
 */
package au.com.wp.corp.p6.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;

/**
 * @author n039126
 *
 */
public class CacheManager {

	private static final Map<String, List<String>> wsHeaders = new HashMap<>();
	
	private static final Map<String, Long>  wsLoginTimestampMap = new HashMap<>();

	public static Map<String, List<String>> getWsHeaders () {
		return wsHeaders;
	}
	
	public static Map<String, Long> getWSLoginTimestamp () {
		return wsLoginTimestampMap;
	}
	
	private static final List<Task> tasksForUpdate = new ArrayList<Task>(); 
	private static final List<String> deletetedExecPkagList = new ArrayList<String>(); 
	private static final List<ExecutionPackage> execPkgListForUpdate = new ArrayList<ExecutionPackage>(); 

	public static List<Task> getTasksforupdate() {
		return tasksForUpdate;
	}

	public static List<String> getDeletetedexecpkaglist() {
		return deletetedExecPkagList;
	}

	public static List<ExecutionPackage> getExecpkglistforupdate() {
		return execPkgListForUpdate;
	}
}
