package au.com.wp.corp.p6.utils;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

import au.com.wp.corp.p6.dto.WorkOrder;

public class ExecutionPackageComparator implements Comparator<WorkOrder> {

	@Override
	public int compare(WorkOrder o1, WorkOrder o2) {
		CompareToBuilder compareBuilder = new CompareToBuilder();
		if ((!StringUtils.isEmpty(o1.getExctnPckgName())) && 
				(!StringUtils.isEmpty(o2.getExctnPckgName()))) {
				 return o1.getExctnPckgName().compareTo(o2.getExctnPckgName());
		}
		return compareBuilder.toComparison();
	}

}
