package au.com.wp.corp.p6.businessservice.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.businessservice.P6MaterialRequisitionService;
import au.com.wp.corp.p6.dataservice.MaterialRequisitionDAO;
import au.com.wp.corp.p6.dto.MaterialRequisitionDTO;
import au.com.wp.corp.p6.dto.MaterialRequisitionRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.elipse.MaterialRequisition;
/**
 * @author n039957
 *
 */
@Service
public class P6MaterialRequisitionServiceImpl implements P6MaterialRequisitionService {
	@Autowired
	private MaterialRequisitionDAO dao;

	@Override
	@Transactional
	public MaterialRequisitionDTO retriveMetReq(MaterialRequisitionRequest input) throws P6BusinessException {
		Object[] workOrderIds = input.getWorkOrderList().toArray();
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		for(Object wo : workOrderIds){
			List<String> reqIds = new ArrayList<String>();
			result.put((String) wo,reqIds);
		}
		List<MaterialRequisition> metReqList = dao.listMetReq(workOrderIds);
		for(MaterialRequisition metReq : metReqList){
			result.get(metReq.getWorkOrder()).add(metReq.getId().getRequisitionNo());
		}
		MaterialRequisitionDTO response = new MaterialRequisitionDTO();
		response.setMaterialRequisitionMap(result);
		return response;
	}
}
