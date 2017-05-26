package au.com.wp.corp.p6.dataservice;

import java.util.List;

import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.elipse.MaterialRequisition;

/**
 * performs database operation on NELL.MSF232 table.It retrieves all the MaterialRequisition
 * 
 * @author n039957
 * @version 1.0
 */
@Repository
public interface MaterialRequisitionDAO extends P6DAOExceptionParser {
	/**
	 * returns list of MaterialRequisition from the MSF232 table
	 * 
	 * @return {@link List<MaterialRequisition>}
	 * @throws P6DataAccessException
	 */
	List<MaterialRequisition> listMetReq(Object[] workOrderId) throws P6DataAccessException;
	
	
}
