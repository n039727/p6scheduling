package au.com.wp.corp.p6.wsclient.ellipse;

import java.util.Collection;
import java.util.List;

import com.mincom.enterpriseservice.ellipse.workordertask.EnterpriseServiceOperationException;
import com.mincom.enterpriseservice.ellipse.workordertask.WorkOrderTaskServiceModifyReplyDTO;
import com.mincom.enterpriseservice.ellipse.workordertask.WorkOrderTaskServiceModifyRequestDTO;

/**
 * Service used for raising work order tasks in Ellipse.
 */
public interface EllipseWorkOrderTaskService {
	
	/**
	 * Updates one or more work order tasks in Ellipse.
	 * 
	 * @param requests Collection of DTO objects containing the information for the work order tasks to be updated.
	 * @param txId An optional transaction ID. If this is to be performed outside of a transaction, <code>null</code>
	 *        should be used instead.
	 * @return List of work order tasks that were updated by Ellipse.
	 * @throws EnterpriseServiceOperationException If there is a problem with the update in Ellipse.
	 */
	public List<WorkOrderTaskServiceModifyReplyDTO> updateWorkOrderTasks(
		Collection<WorkOrderTaskServiceModifyRequestDTO> requests, String txId)
		throws EnterpriseServiceOperationException;
	
}
