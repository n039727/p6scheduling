/**
 * 
 */
package au.com.wp.corp.p6.wsclient.ellipse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mincom.enterpriseservice.ellipse.dependant.dto.WorkOrderDTO;
import com.mincom.enterpriseservice.ellipse.workordertask.EnterpriseServiceOperationException;
import com.mincom.enterpriseservice.ellipse.workordertask.WorkOrderTaskServiceModifyReplyDTO;
import com.mincom.enterpriseservice.ellipse.workordertask.WorkOrderTaskServiceModifyRequestDTO;

/**
 * @author N039126
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/servlet-context.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EllipseIntegrationTest {

	@Autowired
	EllipseWorkOrderTaskService ellipseWorkOrdertaskService;

	@Autowired
	EllipseTransactionService transactionService;

	
	@Test
	public void testEllipseWorkOrdertaskUpdate() throws EnterpriseServiceOperationException {

		final String txId = transactionService.beginTransaction();

		WorkOrderDTO workOrderDTO = new WorkOrderDTO().withNo("790653").withPrefix("04");

		Assert.assertNotNull(txId);

		Collection<WorkOrderTaskServiceModifyRequestDTO> requests = new ArrayList<>();
		WorkOrderTaskServiceModifyRequestDTO request = new WorkOrderTaskServiceModifyRequestDTO()
				.withWorkOrder(workOrderDTO).withWOTaskNo("002").withWorkGroup("MOMT6").withPlanStrDate("20170602")
				.withWOTaskDesc("z0 stdjob").withTaskStatusU("AL");
		requests.add(request);
		try {
			List<WorkOrderTaskServiceModifyReplyDTO >  updatedTasks = ellipseWorkOrdertaskService.updateWorkOrderTasks(requests, txId);
			Assert.assertNotNull(updatedTasks);
			Assert.assertEquals(1, updatedTasks.size());
		} finally {
			transactionService.rollbackTransaction(txId);
		}
	}

}
