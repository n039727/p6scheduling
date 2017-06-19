package au.com.wp.corp.p6.bussiness;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.businessservice.P6MaterialRequisitionService;
import au.com.wp.corp.p6.businessservice.impl.P6MaterialRequisitionServiceImpl;
import au.com.wp.corp.p6.dataservice.MaterialRequisitionDAO;
import au.com.wp.corp.p6.dataservice.impl.MaterialRequisitionDAOImpl;
import au.com.wp.corp.p6.dto.MaterialRequisitionDTO;
import au.com.wp.corp.p6.dto.MaterialRequisitionRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.model.elipse.MaterialRequisition;
import au.com.wp.corp.p6.model.elipse.MaterialRequisitionPK;
import au.com.wp.corp.p6.test.config.AppConfig;
import junit.framework.Assert;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class MaterialRequisitionServiceTest {

	@Mock
	MaterialRequisitionDAO dao;
	
	@InjectMocks
	P6MaterialRequisitionServiceImpl service;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testListMetReq () throws P6BusinessException {
		MaterialRequisitionRequest re = new MaterialRequisitionRequest();
		List<String> workOrderList = new ArrayList<String>();
		workOrderList.add("EC000158");
		workOrderList.add("EC000132");
		workOrderList.add("EC000133");
		re.setWorkOrderList(workOrderList);
		List<MaterialRequisition> resultList = new ArrayList<MaterialRequisition>();
		resultList.add(prepareMetReq("EC000158", "12345678"));
		resultList.add(prepareMetReq("EC000158", "12345679"));
		resultList.add(prepareMetReq("EC000132", "1234567 0000"));
		String[] workOrderIds = re.getWorkOrderList().toArray(new String[re.getWorkOrderList().size()]);
		Mockito.when(dao.listMetReq(workOrderIds)).thenReturn(resultList);
		MaterialRequisitionDTO result = service.retriveMetReq(re);
		Assert.assertNotNull(result.getMaterialRequisitionMap());
	} 
	private MaterialRequisition prepareMetReq(String wo, String reqNo){
		MaterialRequisition marteq1 = new MaterialRequisition();
		MaterialRequisitionPK pk = new MaterialRequisitionPK();
		marteq1.setWorkOrder(wo);
		pk.setRequisitionNo(reqNo);
		pk.setAllocCount("abcde");
		pk.setDstrctCode("ABCDE");
		pk.setReq232Type("AbCdE");
		marteq1.setId(pk);
		return marteq1;
	}
}
