package au.com.wp.corp.p6.businessservice.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

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
		String[] workOrderIds = input.getWorkOrderList().toArray(new String[input.getWorkOrderList().size()]);
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		List<MaterialRequisition> metReqList = dao.listMetReq(workOrderIds);
		for (String wo : workOrderIds) {
			List<MaterialRequisition> mat = findMaterialRequisition(metReqList, wo);
			if (mat != null && mat.size() > 0) {
				mat.forEach(requisition -> {
					if (result.containsKey(wo)) {
						result.get(requisition.getWorkOrder()).add(trimLastFourZeros(requisition.getId().getRequisitionNo()));
					} else {
						List<String> strList = new ArrayList<String>();
						strList.add(trimLastFourZeros(requisition.getId().getRequisitionNo()));
						result.put(wo, strList);
					}
				});

			} else {
				result.put(wo, null);
			}
		}

		MaterialRequisitionDTO response = new MaterialRequisitionDTO();
		response.setMaterialRequisitionMap(result);
		return response;
	}
	private String trimLastFourZeros(String str){
		if(str != null){
			int index = str.lastIndexOf(" 0000");
			if(index > 0){
				str = str.substring(0,index);
			}
		}
		return str;
	}
	private List<MaterialRequisition> findMaterialRequisition(final List<MaterialRequisition> list, final String woId) {
		return list.stream().filter(p -> p.getWorkOrder().equals(woId)).collect(Collectors.toList());
	}
}
