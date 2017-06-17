package au.com.wp.corp.p6.utils;

import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

import au.com.wp.corp.p6.dto.WorkOrder;

public class WorkOrderComparator implements Comparator<WorkOrder> {

	
	@Override
	public int compare(WorkOrder o1, WorkOrder o2) {
		DateUtils dateUtils = new DateUtils();
		Date scheduledDate1 = dateUtils.toDateFromDD_MM_YYYY(o1.getScheduleDate());
		Date scheduledDate2 = dateUtils.toDateFromDD_MM_YYYY(o2.getScheduleDate());
		CompareToBuilder compareBuilder = new CompareToBuilder();

		if ((!StringUtils.isEmpty(o1.getExctnPckgName())) && 
				(!StringUtils.isEmpty(o2.getExctnPckgName()))) {
			compareBuilder.append(o1.getWorkOrderId(), o2.getWorkOrderId());
		}

		compareBuilder.append(scheduledDate1, scheduledDate2);
		return compareBuilder.toComparison();
	}
	
	

}
