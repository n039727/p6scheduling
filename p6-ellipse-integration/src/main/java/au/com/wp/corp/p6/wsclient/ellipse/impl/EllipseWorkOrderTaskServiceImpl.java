package au.com.wp.corp.p6.wsclient.ellipse.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mincom.enterpriseservice.ellipse.dependant.dto.WorkOrderDTO;
import com.mincom.enterpriseservice.ellipse.workordertask.ArrayOfWorkOrderTaskServiceCompleteRequestDTO;
import com.mincom.enterpriseservice.ellipse.workordertask.ArrayOfWorkOrderTaskServiceModifyRequestDTO;
import com.mincom.enterpriseservice.ellipse.workordertask.EnterpriseServiceOperationException;
import com.mincom.enterpriseservice.ellipse.workordertask.WorkOrderTask;
import com.mincom.enterpriseservice.ellipse.workordertask.WorkOrderTaskServiceCompleteReplyCollectionDTO;
import com.mincom.enterpriseservice.ellipse.workordertask.WorkOrderTaskServiceCompleteReplyDTO;
import com.mincom.enterpriseservice.ellipse.workordertask.WorkOrderTaskServiceCompleteRequestDTO;
import com.mincom.enterpriseservice.ellipse.workordertask.WorkOrderTaskServiceModifyReplyCollectionDTO;
import com.mincom.enterpriseservice.ellipse.workordertask.WorkOrderTaskServiceModifyReplyDTO;
import com.mincom.enterpriseservice.ellipse.workordertask.WorkOrderTaskServiceModifyRequestDTO;
import com.mincom.ews.service.connectivity.OperationContext;

import au.com.wp.corp.p6.wsclient.ellipse.EllipseWorkOrderTaskService;

/**
 * Service used for raising work order tasks in Ellipse.
 */
@Service
public class EllipseWorkOrderTaskServiceImpl implements EllipseWorkOrderTaskService {
	
	private static final Logger LOG = LoggerFactory.getLogger(EllipseWorkOrderTaskServiceImpl.class);
	
	private WorkOrderTask taskService;
	
	/**
	 * Updates one or more work order tasks in Ellipse.
	 * 
	 * @param requests Collection of DTO objects containing the information for the work order tasks to be updated.
	 * @param txId An optional transaction ID. If this is to be performed outside of a transaction, <code>null</code>
	 *        should be used instead.
	 * @return List of work order tasks that were updated by Ellipse.
	 * @throws EnterpriseServiceOperationException If there is a problem with the update in Ellipse.
	 */
	@Override
	public List<WorkOrderTaskServiceModifyReplyDTO> updateWorkOrderTasks(
		Collection<WorkOrderTaskServiceModifyRequestDTO> requests, String txId)
		throws EnterpriseServiceOperationException {
		LOG.debug(String.format("Request to update %d work order tasks in Ellipse in transaction \"%s\".",
			requests.size(), txId));
		
		// Create the request parameters to pass to the Ellipse work order service.
		OperationContext ctx = EllipseWebServiceHelper.generateContext(txId);
		ArrayOfWorkOrderTaskServiceModifyRequestDTO requestParams = new ArrayOfWorkOrderTaskServiceModifyRequestDTO()
			.withWorkOrderTaskServiceModifyRequestDTO(requests);
		
		// Call the web service to update the work order tasks.
		WorkOrderTaskServiceModifyReplyCollectionDTO reply = taskService.multipleModify(ctx, requestParams);
		
		// Get the work order task information from the reply.
		if ((reply == null) || (reply.getReplyElements() == null)) {
			// We didn't get anything back.
			LOG.debug("No work orders updated.");
			return new ArrayList<WorkOrderTaskServiceModifyReplyDTO>();
		} else {
			List<WorkOrderTaskServiceModifyReplyDTO> results = reply.getReplyElements()
				.getWorkOrderTaskServiceModifyReplyDTO();
			LOG.debug(String.format("Modified %d work orders.", results.size()));
			return results;
		}
	}

	@Autowired
	public void setTaskService(WorkOrderTask taskService) {
		this.taskService = taskService;
	}
}
