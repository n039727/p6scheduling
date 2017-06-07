package au.com.wp.corp.p6.wsclient.ellipse;

import java.util.Collection;
import java.util.List;

import com.mincom.enterpriseservice.ellipse.dependant.dto.WorkOrderDTO;
import com.mincom.enterpriseservice.ellipse.workordertask.EnterpriseServiceOperationException;
import com.mincom.enterpriseservice.ellipse.workordertask.WorkOrderTaskServiceCompleteReplyDTO;
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
	
	/**
	 * Closes one or more work order tasks in Ellipse.
	 * 
	 * @param closedDate The date to use for when the work order tasks have been closed.
	 * @param closedTime The time to use for when the work order tasks have been closed.
	 * @param completedBy The user to record as closing the work order tasks.
	 * @param taskNumber The task number of the tasks to be closed.
	 * @param workOrders The list of work orders for which the tasks are being closed.
	 * @param txId An optional transaction ID. If this is to be performed outside of a transaction, <code>null</code>
	 *        should be used instead.
	 * @return List of work order tasks that were closed by Ellipse.
	 * @throws EnterpriseServiceOperationException If there is a problem with the task closure in Ellipse.
	 */
	public List<WorkOrderTaskServiceCompleteReplyDTO> closeWorkOrderTasks(String closedDate, String closedTime,
		String completedBy, String taskNumber, List<WorkOrderDTO> workOrders, String txId)
		throws EnterpriseServiceOperationException;
}
