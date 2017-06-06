package au.com.wp.corp.p6.wsclient.ellipse.impl;

import java.util.Collection;
import java.util.List;

import com.mincom.enterpriseservice.ellipse.workorder.EnterpriseServiceOperationException;
import com.mincom.enterpriseservice.ellipse.workorder.WorkOrderServiceCreateReplyDTO;
import com.mincom.enterpriseservice.ellipse.workorder.WorkOrderServiceCreateRequestDTO;
import com.mincom.enterpriseservice.ellipse.workorder.WorkOrderServiceReadReplyDTO;
import com.mincom.enterpriseservice.ellipse.workorder.WorkOrderServiceReadRequestDTO;


/**
 * Service used for raising work orders in Ellipse.
 */
public interface EllipseWorkOrderService {
	/**
	 * Creates one or more work orders in Ellipse.
	 * 
	 * @param requests Collection of DTO objects containing the information for the work orders to be created.
	 * @param txId An optional transaction ID. If this is to be performed outside of a transaction, <code>null</code>
	 *        should be used instead.
	 * @return List of work orders that were created by Ellipse.
	 * @throws EnterpriseServiceOperationException If there is a problem with the creation in Ellipse.
	 */
	public List<WorkOrderServiceCreateReplyDTO> createWorkOrders(Collection<WorkOrderServiceCreateRequestDTO> requests,
		String txId) throws EnterpriseServiceOperationException;

	public List<WorkOrderServiceReadReplyDTO> readWorkOrders(Collection<WorkOrderServiceReadRequestDTO> requests, String txId) throws EnterpriseServiceOperationException;
}
