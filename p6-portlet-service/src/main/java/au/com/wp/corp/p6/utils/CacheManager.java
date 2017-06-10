/**
 * 
 */
package au.com.wp.corp.p6.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	private static final Set<Task> tasksForUpdate = new HashSet<Task>(); 
	private static final Set<String> deletetedExecPkagList = new HashSet<String>(); 
	private static final Set<ExecutionPackage> execPkgListForUpdate = new HashSet<ExecutionPackage>(); 

	public static Set<Task> getTasksforupdate() {
		return tasksForUpdate;
	}

	public static Set<String> getDeletetedexecpkaglist() {
		return deletetedExecPkagList;
	}

	public static Set<ExecutionPackage> getExecpkglistforupdate() {
		return execPkgListForUpdate;
	}
}
