package au.com.wp.corp.p6.dataservice;

import java.util.List;
import java.util.Map;

public interface ResourceDetailDAO extends P6DAOExceptionParser{
	
	Map<String, List<String>> fetchAllResourceDetail();

}
