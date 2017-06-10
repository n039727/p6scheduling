/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValue;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValueFieldType;
import au.com.wp.corp.p6.wsclient.udfvalue.IntegrationFault;

/**
 * @author n039126
 *
 */
public class ReadUDFValueServiceCall extends UDFValueServiceCall<List<UDFValue>> {
	private static final Logger logger1 = LoggerFactory.getLogger(ReadUDFValueServiceCall.class);
	
	private String filter;
	private String orderBy;
	public ReadUDFValueServiceCall(final RequestTrackingId trackingId, String filter, String orderBy) {
		super(trackingId);
		this.filter = filter;
		this.orderBy = orderBy;
	}

	@Override
	protected Holder<List<UDFValue>> command() throws P6ServiceException {
		
		List<UDFValueFieldType> fields = new ArrayList<>();
		
		fields.add(UDFValueFieldType.FOREIGN_OBJECT_ID );
		fields.add(UDFValueFieldType.UDF_TYPE_OBJECT_ID );
		fields.add(UDFValueFieldType.PROJECT_OBJECT_ID);
		fields.add(UDFValueFieldType.UDF_TYPE_SUBJECT_AREA);
		fields.add(UDFValueFieldType.UDF_TYPE_TITLE);
		fields.add(UDFValueFieldType.TEXT);
		fields.add(UDFValueFieldType.DOUBLE);
		List<UDFValue> retValues = null;
		try {
			logger1.debug("calling readUDFValues with filter == {} and fields {}",filter,fields);
			retValues = servicePort.readUDFValues(fields, filter, orderBy);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		}
		return new Holder<>(retValues);
	}


}
