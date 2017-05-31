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
		Object[] workOrderId = input.getWorkOrderList().toArray();
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		List<MaterialRequisition> metReqList = dao.listMetReq(workOrderId);
		for(Object wo : workOrderId){
			Optional<MaterialRequisition> s = findTask(metReqList, (String) wo);
			if(s.isPresent()){
				MaterialRequisition metReq = s.get();
				if(result.containsKey(metReq.getWorkOrder())){
					result.get(metReq.getWorkOrder()).add(metReq.getId().getRequisitionNo());
				}else{
					List<String> reqIds = new ArrayList<String>();
					reqIds.add(metReq.getId().getRequisitionNo());
					result.put(metReq.getWorkOrder(),reqIds);
				}
			}else{
				result.put((String) wo, new ArrayList<String>());
			}
		}
		MaterialRequisitionDTO response = new MaterialRequisitionDTO();
		response.setMaterialRequisitionMap(result);
		return response;
	}

	private Optional<MaterialRequisition> findTask(final List<MaterialRequisition> list, final String woId) {
		return list.stream()
				.filter(p -> p.getWorkOrder().equals(woId)).findAny();
	}
}
