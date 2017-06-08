/**
 * 
 */
package au.com.wp.corp.p6.wsclient.ellipse.impl;

import java.util.ArrayList;
import java.util.Collection;
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
import com.mincom.enterpriseservice.ellipse.workordertask.EnterpriseServiceOperationException;
import com.mincom.enterpriseservice.ellipse.workordertask.WorkOrderTaskServiceModifyRequestDTO;

import au.com.wp.corp.p6.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.util.DateUtil;
import au.com.wp.corp.p6.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.wsclient.constant.P6EllipseWSConstants;
import au.com.wp.corp.p6.wsclient.ellipse.EllipseWSClient;
import au.com.wp.corp.p6.wsclient.ellipse.EllipseWorkOrderTaskService;

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
	EllipseWorkOrderTaskService ellipseWorkOrdertaskService;


	/*
	 * (non-Javadoc)
	 * 
	 * @see au.com.wp.corp.p6.wsclient.ellipse.EllipseWSClient#
	 * updateActivitiesEllipse(java.util.List)
	 */
	@Override
	public void updateActivitiesEllipse(List<EllipseActivityDTO> activities) throws P6ServiceException {
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

		Collection<WorkOrderTaskServiceModifyRequestDTO> requests;
		WorkOrderTaskServiceModifyRequestDTO workorderTaskService;
		WorkOrderDTO workOrder;
		Map<String, String> workorderTask;
		List<EllipseActivityDTO> ellipseActivities;
		int noOfIteration = (activities.size() / noOfActvtyTobeProccessedAtATime) + 1;
		for (int i = 0; i < noOfIteration; i++) {
			requests = new ArrayList<>();
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
				workorderTaskService = new WorkOrderTaskServiceModifyRequestDTO();
				workorderTaskService.setWorkOrder(workOrder);
				workorderTaskService.setWOTaskNo(workorderTask.get(TASK_NO));
				if (null != activity.getWorkGroup() && !activity.getWorkGroup().trim().isEmpty())
					workorderTaskService.setWorkGroup(activity.getWorkGroup());
				if (null != activity.getPlannedStartDate())
					workorderTaskService.setPlanStrDate(dateUtil.convertDateToString(activity.getPlannedStartDate(), DateUtil.ELLIPSE_DATE_FORMAT, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));

				if (null != activity.getTaskUserStatus() && !activity.getTaskUserStatus().trim().isEmpty())
					workorderTaskService.setTaskStatusU(activity.getTaskUserStatus());

				requests.add(workorderTaskService);
			}
			try {
				logger.debug("Updating ellipse with list of work order task -{}", requests.size() );
				ellipseWorkOrdertaskService.updateWorkOrderTasks(requests, null);
			} catch (EnterpriseServiceOperationException e) {
				logger.error("An error occurs while updating Ellipse", e);
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
