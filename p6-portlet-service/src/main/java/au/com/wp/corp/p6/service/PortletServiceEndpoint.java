/**
 * 
 */
package au.com.wp.corp.p6.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import au.com.wp.corp.p6.dto.MaterialRequisitionDTO;
import au.com.wp.corp.p6.dto.MaterialRequisitionRequest;
import au.com.wp.corp.p6.dto.MetadataDTO;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BaseException;
import au.com.wp.corp.p6.exception.P6BusinessException;

/**
 * @author n039619
 *
 */
public interface PortletServiceEndpoint {
	
	ViewToDoStatus fetchWorkOrdersForViewToDoStatus(RequestEntity<WorkOrderSearchRequest> query) throws P6BusinessException;
	ResponseEntity<WorkOrder> saveWorkOrder(RequestEntity<WorkOrder> workOrder, HttpServletRequest request) throws P6BusinessException;

	public MetadataDTO  fetchMetadata()throws P6BusinessException;
	ResponseEntity<ViewToDoStatus> saveViewToDoStatus(RequestEntity<ViewToDoStatus> viewToDoStatus, HttpServletRequest request) throws P6BusinessException;
	List<WorkOrder> fetchWorkOrdersForAddUpdateToDo(RequestEntity<WorkOrderSearchRequest> query) throws P6BusinessException;
	ResponseEntity<MaterialRequisitionDTO> fetchMetReqdata(RequestEntity<MaterialRequisitionRequest> request) throws P6BaseException;

}
