/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.wsclient.cleint.qualifier.ReadUDFValue;
import au.com.wp.corp.p6.wsclient.udfvalue.IntegrationFault;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValue;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValueFieldType;

/**
 * @author n039126
 *
 */
@Component
@ReadUDFValue
@Lazy
public class ReadUDFValueServiceCall extends UDFValueServiceCall<List<UDFValue>> {
	private static final Logger logger1 = LoggerFactory.getLogger(ReadUDFValueServiceCall.class);
	
	private String filter;
	private String orderBy;
	public ReadUDFValueServiceCall() throws P6ServiceException {
		super();
	}

	@Override
	protected List<UDFValue> command() throws P6ServiceException {
		
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
		} finally {
			this.filter = null;
			this.orderBy = null;
		}
		return retValues;
	}

	@Override
	public List<UDFValue> readUDFValues(final String filter, final String orderBy) throws P6ServiceException{
		this.filter = filter;
		this.orderBy = orderBy;
		return run();
	}

}
