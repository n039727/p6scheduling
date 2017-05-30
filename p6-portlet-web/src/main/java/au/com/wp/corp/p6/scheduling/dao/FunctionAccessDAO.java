package au.com.wp.corp.p6.scheduling.dao;

import java.util.List;

import au.com.wp.corp.p6.scheduling.exception.P6DAOExceptionParser;
import au.com.wp.corp.p6.scheduling.model.FunctionAccess;

public interface FunctionAccessDAO extends P6DAOExceptionParser{
	
	public List<FunctionAccess> getAccess(String roleName);

}
