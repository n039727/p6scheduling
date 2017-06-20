/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.ellipse.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mincom.enterpriseservice.ellipse.dependant.dto.WorkOrderDTO;
import com.mincom.enterpriseservice.ellipse.workordertask.ArrayOfWorkOrderTaskServiceModifyRequestDTO;
import com.mincom.enterpriseservice.ellipse.workordertask.MultipleModify;
import com.mincom.enterpriseservice.ellipse.workordertask.MultipleModifyResponse;
import com.mincom.enterpriseservice.ellipse.workordertask.WorkOrderTaskServiceModifyRequestDTO;
import com.mincom.ews.service.connectivity.OperationContext;
import com.mincom.ews.service.connectivity.RunAs;
import com.mincom.ews.service.transaction.Begin;
import com.mincom.ews.service.transaction.BeginResponse;
import com.mincom.ews.service.transaction.Commit;
import com.mincom.ews.service.transaction.CommitResponse;
import com.mincom.ews.service.transaction.Rollback;
import com.mincom.ews.service.transaction.RollbackResponse;

import au.com.wp.corp.integration.ellipsews.transaction.TransactionWsClient;
import au.com.wp.corp.integration.ellipsews.workordertask.WorkOrderTaskWsClient;
import au.com.wp.corp.p6.integration.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.DateUtil;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.integration.wsclient.constant.P6EllipseWSConstants;
import au.com.wp.corp.p6.integration.wsclient.ellipse.EllipseWSClient;

/**
 * @author N039126
 * @version 1.0
 */
@Service
public class EllipseWSClientImpl implements EllipseWSClient {

	private static final Logger logger = LoggerFactory.getLogger(EllipseWSClientImpl.class);

	private static final String PREFIX = "PREFIX";

	private static final String WORK_ORDER = "WORK_ORDER";

	private static final String TASK_NO = "TASK_NO";

	private static final String PATTERN_STRING = "(^[A-Z0-9]{2})([A-Z0-9]{6})([A-Z0-9]{3})";

	@Autowired
	DateUtil dateUtil;

	@Autowired
	WorkOrderTaskWsClient workOrderTaskWsClient;

	@Autowired
	TransactionWsClient transactionWsClient;

	public String startTransaction() {
		Begin begin = new Begin();
		OperationContext beginOperationContext = new OperationContext();
		beginOperationContext.setDistrict("CORP");
		beginOperationContext.setRunAs(new RunAs());
		begin.setContext(beginOperationContext);
		BeginResponse beginResponse = transactionWsClient.begin(begin);
		return beginResponse.getTransactionId();
	}

	public void rollbackTransaction(String transactionId) {
		Rollback rollback = new Rollback();
		OperationContext rollbackOperationContext = new OperationContext();
		rollbackOperationContext.setDistrict("CORP");
		rollbackOperationContext.setRunAs(new RunAs());
		rollbackOperationContext.setTransaction(transactionId);
		rollback.setContext(rollbackOperationContext);
		RollbackResponse rollbackResponse = transactionWsClient.rollback(rollback);
	}

	public void commitTransaction(String transactionId) {
		Commit commit = new Commit();
		OperationContext commitOperationContext = new OperationContext();
		commitOperationContext.setDistrict("CORP");
		commitOperationContext.setRunAs(new RunAs());
		commitOperationContext.setTransaction(transactionId);
		commit.setContext(commitOperationContext);
		CommitResponse commitResponse = transactionWsClient.commit(commit);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see au.com.wp.corp.p6.wsclient.ellipse.EllipseWSClient#
	 * updateActivitiesEllipse(java.util.List)
	 */
	@Override
	public void updateActivitiesEllipse(List<EllipseActivityDTO> activities)
			throws P6ServiceException {
		logger.info("Updating activities in Ellipse..");
		int noOfActvtyTobeProccessedAtATime;
		try {
			noOfActvtyTobeProccessedAtATime = Integer.valueOf(P6ReloadablePropertiesReader
					.getProperty(P6EllipseWSConstants.NO_ACTVTY_TO_BE_PRCSSD_ATATIME_IN_ELLIPSE));
		} catch (NumberFormatException e1) {
			throw new P6ServiceException(e1);
		}

		logger.debug("Number of activties to be updated in Ellipse in a single service call #{}",
				noOfActvtyTobeProccessedAtATime);

		List<EllipseActivityDTO> ellipseActivities;
		int noOfIteration = (activities.size() / noOfActvtyTobeProccessedAtATime) + 1;
		for (int i = 0; i < noOfIteration; i++) {
			String transId =startTransaction();
			MultipleModify multipleModify = new MultipleModify();
			OperationContext operationContext = new OperationContext();
			operationContext.setRunAs(new RunAs());
			operationContext.setDistrict("CORP");
			operationContext.setTransaction(transId);
			multipleModify.setContext(operationContext);

			ArrayOfWorkOrderTaskServiceModifyRequestDTO arrayModify;
			WorkOrderTaskServiceModifyRequestDTO woTaskModifyDTO;
			WorkOrderDTO workOrder;
			Map<String, String> workorderTask;
			
			
			arrayModify = new ArrayOfWorkOrderTaskServiceModifyRequestDTO();
			int startIndex = i * noOfActvtyTobeProccessedAtATime;
			int endIndex = ((i + 1) * noOfActvtyTobeProccessedAtATime - 1) < activities.size()
					? ((i + 1) * noOfActvtyTobeProccessedAtATime - 1) : activities.size();

			logger.debug("constructing activity start index # {}  - end index # {}", startIndex, endIndex);

			ellipseActivities = activities.subList(startIndex, endIndex);
			for (EllipseActivityDTO activity : ellipseActivities) {
				workorderTask = getWorkOrderNoWithPrefixAndTask(activity.getWorkOrderTaskId());
				workOrder = new WorkOrderDTO();
				workOrder.setNo(workorderTask.get(WORK_ORDER));
				workOrder.setPrefix(workorderTask.get(PREFIX));
				woTaskModifyDTO = new WorkOrderTaskServiceModifyRequestDTO();
				woTaskModifyDTO.setWorkOrder(workOrder);
				woTaskModifyDTO.setWOTaskNo(workorderTask.get(TASK_NO));
				if (null != activity.getWorkGroup() && !activity.getWorkGroup().trim().isEmpty())
					woTaskModifyDTO.setWorkGroup(activity.getWorkGroup());
				
				final String plantStrDate = dateUtil.convertDateToString(activity.getPlannedStartDate(),
						DateUtil.ELLIPSE_DATE_FORMAT, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP);
				
				if (null != plantStrDate && !plantStrDate.trim().isEmpty())
					woTaskModifyDTO.setPlanStrDate(plantStrDate);
				else if ( null == activity.getPlannedStartDate() && null == activity.getPlannedFinishDate() ){
					woTaskModifyDTO.setPlanStrDate("");
					woTaskModifyDTO.setPlanFinDate("");
				}
				if (null != activity.getTaskUserStatus() && !activity.getTaskUserStatus().trim().isEmpty())
					woTaskModifyDTO.setTaskStatusU(activity.getTaskUserStatus());

				arrayModify.getWorkOrderTaskServiceModifyRequestDTO().add(woTaskModifyDTO);
				multipleModify.setRequestParameters(arrayModify);
			}
			logger.debug("Updating ellipse with list of work order task -{}",
					arrayModify.getWorkOrderTaskServiceModifyRequestDTO().size());
			try {
				MultipleModifyResponse response = workOrderTaskWsClient.multipleModify(multipleModify);
				if ( response.getOut() != null)
				commitTransaction(transId);
			} catch (Exception e) {
				rollbackTransaction(transId);
				throw new P6ServiceException(e);
			}
		}

	}

	private Map<String, String> getWorkOrderNoWithPrefixAndTask(String workorderTaskId) {
		final Map<String, String> workOrderMap = new HashMap<>();
		final Pattern pattern = Pattern.compile(PATTERN_STRING);
		final Matcher matcher = pattern.matcher(workorderTaskId);
		if (matcher.matches()) {
			workOrderMap.put(PREFIX, matcher.group(1));
			workOrderMap.put(WORK_ORDER, matcher.group(2));
			workOrderMap.put(TASK_NO, matcher.group(3));
		}
		logger.debug("Workorder task - {} after tokenize # {}", workorderTaskId, workOrderMap);
		return workOrderMap;
	}

}
