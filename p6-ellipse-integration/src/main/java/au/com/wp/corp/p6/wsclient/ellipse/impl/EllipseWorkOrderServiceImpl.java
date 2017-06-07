package au.com.wp.corp.p6.wsclient.ellipse.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mincom.enterpriseservice.ellipse.workorder.EnterpriseServiceOperationException;
import com.mincom.enterpriseservice.ellipse.workorder.ArrayOfWorkOrderServiceCreateRequestDTO;
import com.mincom.enterpriseservice.ellipse.workorder.ArrayOfWorkOrderServiceReadRequestDTO;
import com.mincom.enterpriseservice.ellipse.workorder.WorkOrder;
import com.mincom.enterpriseservice.ellipse.workorder.WorkOrderServiceCreateReplyCollectionDTO;
import com.mincom.enterpriseservice.ellipse.workorder.WorkOrderServiceCreateReplyDTO;
import com.mincom.enterpriseservice.ellipse.workorder.WorkOrderServiceCreateRequestDTO;
import com.mincom.enterpriseservice.ellipse.workorder.WorkOrderServiceReadReplyCollectionDTO;
import com.mincom.enterpriseservice.ellipse.workorder.WorkOrderServiceReadReplyDTO;
import com.mincom.enterpriseservice.ellipse.workorder.WorkOrderServiceReadRequestDTO;
import com.mincom.ews.service.connectivity.OperationContext;

/**
 * Service used for raising work orders in Ellipse.
 * 
 * this is done via the mincom interfaces.
 */
@Service
public class EllipseWorkOrderServiceImpl implements EllipseWorkOrderService {
	
	private static final Logger LOG = LoggerFactory.getLogger(EllipseWorkOrderServiceImpl.class);
	
	private WorkOrder workOrderService;
	
	/**
	 * Creates one or more work orders in Ellipse.
	 * 
	 * @param requests Collection of DTO objects containing the information for the work orders to be created.
	 * @param txId An optional transaction ID. If this is to be performed outside of a transaction, <code>null</code>
	 *        should be used instead.
	 * @return List of work orders that were created by Ellipse.
	 * @throws EnterpriseServiceOperationException If there is a problem with the creation in Ellipse.
	 */
	@Override
	public List<WorkOrderServiceCreateReplyDTO> createWorkOrders(Collection<WorkOrderServiceCreateRequestDTO> requests,
		String txId) throws EnterpriseServiceOperationException {
		LOG.debug(String.format("Request to create %d work orders in Ellipse in transaction \"%s\".", requests.size(),
			txId));

		// Create the request parameters to pass to the Ellipse work order service.
		OperationContext ctx = EllipseWebServiceHelper.generateContext(txId);
		ArrayOfWorkOrderServiceCreateRequestDTO requestParams = new ArrayOfWorkOrderServiceCreateRequestDTO()
			.withWorkOrderServiceCreateRequestDTO(requests);
		
		// Call the web service to create the work orders.
		WorkOrderServiceCreateReplyCollectionDTO reply = workOrderService.multipleCreate(ctx, requestParams);
		
		// Get the work order information from the reply.
		if ((reply == null) || (reply.getReplyElements() == null)) {
			// We didn't get anything back.
			return new ArrayList<WorkOrderServiceCreateReplyDTO>();
		} else {
			return reply.getReplyElements().getWorkOrderServiceCreateReplyDTO();
		}
	}
	
	
	/**
	 * Reads one or more work orders in Ellipse.
	 * 
	 * @param requests Collection of DTO objects containing the information for the work orders to be created.
	 * @param txId An optional transaction ID. If this is to be performed outside of a transaction, <code>null</code>
	 *        should be used instead.
	 * @return List of work orders that were created by Ellipse.
	 * @throws EnterpriseServiceOperationException If there is a problem with the creation in Ellipse.
	 */
	@Override
	public List<WorkOrderServiceReadReplyDTO> readWorkOrders(Collection<WorkOrderServiceReadRequestDTO> requests, String txId) throws EnterpriseServiceOperationException {
		LOG.debug(String.format("Request to read %d work orders in Ellipse in transaction \"%s\".", requests.size(),
			txId));

		// Create the request parameters to pass to the Ellipse work order service.
		OperationContext ctx = EllipseWebServiceHelper.generateContext(txId);
		ArrayOfWorkOrderServiceReadRequestDTO requestParams = new ArrayOfWorkOrderServiceReadRequestDTO()
			.withWorkOrderServiceReadRequestDTO(requests);
		
		// Call the web service to create the work orders.
		WorkOrderServiceReadReplyCollectionDTO reply = workOrderService.multipleRead(ctx, requestParams);
		
		// Get the work order information from the reply.
		if ((reply == null) || (reply.getReplyElements() == null)) {
			// We didn't get anything back.
			return new ArrayList<WorkOrderServiceReadReplyDTO>();
		} else {
			return reply.getReplyElements().getWorkOrderServiceReadReplyDTO();
		}
	}


	@Autowired
	public void setWorkOrderService(WorkOrder workOrderService) {
		this.workOrderService = workOrderService;
	}
}
