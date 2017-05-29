package au.com.wp.corp.p6.businessservice;

import java.util.List;
import java.util.Map;

import au.com.wp.corp.p6.dto.MaterialRequisitionDTO;
import au.com.wp.corp.p6.dto.MaterialRequisitionRequest;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;

public interface P6MaterialRequisitionService {
	public MaterialRequisitionDTO retriveMetReq(MaterialRequisitionRequest input) throws P6BusinessException;
}
