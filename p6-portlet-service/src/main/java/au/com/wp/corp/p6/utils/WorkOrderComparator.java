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
		// int c;
		// c = scheduledDate1.compareTo(scheduledDate2);
		// if (c == 0){
		if ((!StringUtils.isEmpty(o1.getExctnPckgName())) && (!StringUtils.isEmpty(o2.getExctnPckgName()))) {
			/*
			 * Date executionPkgNam1 =
			 * dateUtils.toDateFromYYYY_MM_DD(o1.getExctnPckgName().split("_")[0
			 * ]); Date executionPkgNam2 =
			 * dateUtils.toDateFromYYYY_MM_DD(o2.getExctnPckgName().split("_")[0
			 * ]);
			 */

			/*
			 * Date executionPkgNam1 =
			 * dateUtils.toDateFromDDMMYYYY(o1.getExctnPckgName().substring(0,
			 * 8)); Date executionPkgNam2 =
			 * dateUtils.toDateFromDDMMYYYY(o2.getExctnPckgName().substring(0,
			 * 8)); compareBuilder.append(executionPkgNam1, executionPkgNam2);
			 */
			compareBuilder.append(o1.getWorkOrderId(), o2.getWorkOrderId());
		}
		// }
		compareBuilder.append(scheduledDate1, scheduledDate2);

		return compareBuilder.toComparison();
	}

}
