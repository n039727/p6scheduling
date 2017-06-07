package au.com.wp.corp.p6.wsclient.ellipse.impl;

import com.mincom.ews.service.connectivity.OperationContext;

public class EllipseWebServiceHelper {
	
	/**
	 * The date format used for Ellipse date fields.
	 */
	public static final String ELLIPSE_DATE_FORMAT = "yyyyMMdd";
	
	/**
	 * The date format used for Ellipse time fields.
	 */
	public static final String ELLIPSE_TIME_FORMAT = "HHmmss";
	
	/**
	 * The employee ID to use in Ellipse for system generated requests.
	 */
	public static final String SYSTEM_ELLIPSE_ID = "SYSIMPDX";
	
	/**
	 * Generates a new context for use in calls to Ellipse web services. 
	 * 
	 * @param txId The transaction ID to use for the context, or <code>null</code> if none is desired.
	 * @return The {@link OperationContext} object for use with Ellipse web service calls.
	 */
	public static OperationContext generateContext(String txId) {
		OperationContext ctx = new OperationContext()
			.withDistrict("CORP")
			.withMaxInstances(20)
			.withReturnWarnings(false)
			.withTrace(false);
		
		if (txId != null) {
			ctx.setTransaction(txId);
		}
		
		return ctx;
	}
}
