package au.com.wp.corp.p6.dataservice;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.elipse.MaterialRequisition;
import au.com.wp.corp.p6.test.config.AppConfig;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class MaterialRequisitionDAOTest {
	
	@Autowired
	MaterialRequisitionDAO dao;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Ignore
	@Transactional
	@Rollback(true)
	public void testListMetReq () throws P6DataAccessException {
		String[] workOrderId = new String[] {"EC000133","EC000132","EC000158","EC000134"};
		List<MaterialRequisition> reqs = dao.listMetReq(workOrderId);
		Assert.assertNotNull(reqs);
		for ( MaterialRequisition req : reqs ){
			Assert.assertNotNull(req.getId().getRequisitionNo());
		}
	}

}
