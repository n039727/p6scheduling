/**
 * 
 */
package au.com.wp.corp.p6.wsclient.ellipse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mincom.enterpriseservice.ellipse.dependant.dto.WorkOrderDTO;
import com.mincom.enterpriseservice.ellipse.workorder.WorkOrderServiceReadReplyDTO;
import com.mincom.enterpriseservice.ellipse.workorder.WorkOrderServiceReadRequestDTO;
import com.mincom.enterpriseservice.ellipse.workordertask.EnterpriseServiceOperationException;
import com.mincom.enterpriseservice.ellipse.workordertask.WorkOrderTaskServiceModifyRequestDTO;

import au.com.wp.corp.p6.wsclient.ellipse.impl.EllipseWorkOrderService;

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

	@Autowired
	EllipseWorkOrderService workorderService;

	@Before
	public void setUp() throws Exception {
		// MockitoAnnotations.initMocks(this);

	}

	@Test
	@Ignore
	public void testEllipseWorkOrdertaskUpdate() throws EnterpriseServiceOperationException {

		final String txId = transactionService.beginTransaction();

		WorkOrderDTO workOrderDTO = new WorkOrderDTO().withNo("04790653");

		List<WorkOrderServiceReadRequestDTO> readReqs = new ArrayList<>();
		WorkOrderServiceReadRequestDTO readReq = new WorkOrderServiceReadRequestDTO();
		readReq.setIncludeTasks(true);
		readReq.setWorkOrder(workOrderDTO);
		readReqs.add(readReq);

		List<WorkOrderServiceReadReplyDTO> replyDtos = null;
		try {
			replyDtos = workorderService.readWorkOrders(readReqs, txId);
		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("work orders = " + replyDtos);

		System.out.println("Transaction ID - " + txId);

		Collection<WorkOrderTaskServiceModifyRequestDTO> requests = new ArrayList<>();
		WorkOrderTaskServiceModifyRequestDTO request = new WorkOrderTaskServiceModifyRequestDTO()
				.withWorkOrder(workOrderDTO).withWOTaskNo("002").withWorkGroup("MOMT6").withPlanStrDate("20170602")
				.withWOTaskDesc("z0 stdjob").withTaskStatusU("AL");
		requests.add(request);
		try {
			ellipseWorkOrdertaskService.updateWorkOrderTasks(requests, txId);
			System.out.println("Transaction successfull....");
		} finally {
			System.out.println("Rolling back the transaction ");
			transactionService.rollbackTransaction(txId);
		}
	}

}
