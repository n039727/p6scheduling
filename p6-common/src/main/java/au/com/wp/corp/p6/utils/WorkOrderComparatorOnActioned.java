package au.com.wp.corp.p6.utils;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

import au.com.wp.corp.p6.dto.WorkOrder;


public class WorkOrderComparatorOnActioned implements Comparator<WorkOrder> {

	@Override
	public int compare(WorkOrder o1, WorkOrder o2) {
		CompareToBuilder compareBuilder = new CompareToBuilder();

		compareBuilder.append(o1.getActioned(),o2.getActioned());

		return compareBuilder.toComparison();
	}


}
