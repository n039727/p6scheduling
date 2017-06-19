package au.com.wp.corp.p6.businessservice;

import au.com.wp.corp.p6.dto.MaterialRequisitionDTO;
import au.com.wp.corp.p6.dto.MaterialRequisitionRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;

public interface P6MaterialRequisitionService {
	public MaterialRequisitionDTO retriveMetReq(MaterialRequisitionRequest input) throws P6BusinessException;
}
