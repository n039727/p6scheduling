/**
 * 
 */
package au.com.wp.corp.p6.dataservice;

import au.com.wp.corp.p6.model.ExecutionPackage;

/**
 * @author n039619
 *
 */
public interface ExecutionPackageDao {
	
	ExecutionPackage fetch(String name);

}
