package au.com.wp.corp.p6.utils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.wp.corp.p6.dto.WorkOrder;

public class ExecutionPackageComparatorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCompare() {
		WorkOrder workOrder1 = new WorkOrder();
		workOrder1.setExctnPckgName("123456789");
		
		WorkOrder workOrder2 = new WorkOrder();
		workOrder2.setExctnPckgName("123456789");
		
		ExecutionPackageComparator executionPackageComparator = new ExecutionPackageComparator();
		
		int result = executionPackageComparator.compare(workOrder1, workOrder2);
		Assert.assertNotNull(result);
	}

}
