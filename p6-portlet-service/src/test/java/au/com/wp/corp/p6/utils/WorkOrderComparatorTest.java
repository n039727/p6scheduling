package au.com.wp.corp.p6.utils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.test.config.AppConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
public class WorkOrderComparatorTest {

		
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCompare() {
		
		WorkOrder workOrder1 = new WorkOrder();
		workOrder1.setScheduleDate("18/04/2017");
		
		WorkOrder workOrder2 = new WorkOrder();
		workOrder2.setScheduleDate("19/04/2017");
		
		WorkOrderComparator workOrderComparator = new WorkOrderComparator();
		
		int result = workOrderComparator.compare(workOrder1, workOrder2);
		Assert.assertNotNull(result);
	}

}
