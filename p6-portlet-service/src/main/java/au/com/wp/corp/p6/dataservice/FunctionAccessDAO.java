package au.com.wp.corp.p6.dataservice;

import java.util.List;

import au.com.wp.corp.p6.model.FunctionAccess;

public interface FunctionAccessDAO extends P6DAOExceptionParser{
	
	public List<FunctionAccess> getAccess(String roleName);
	public List<String> fetchAllRole();

}
