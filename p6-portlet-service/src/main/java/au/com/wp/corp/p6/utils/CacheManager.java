/**
 * 
 */
package au.com.wp.corp.p6.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
}
