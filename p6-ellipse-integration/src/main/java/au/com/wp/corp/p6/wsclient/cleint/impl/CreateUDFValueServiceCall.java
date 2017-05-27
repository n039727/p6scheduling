/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.util.List;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValuesResponse.ObjectId;
import au.com.wp.corp.p6.wsclient.udfvalue.IntegrationFault;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValue;

/**
 * @author n039126
 *
 */
public class CreateUDFValueServiceCall extends UDFValueServiceCall<List<ObjectId>> {
	private static final Logger logger = LoggerFactory.getLogger(CreateUDFValueServiceCall.class);
	
	private final List<UDFValue> udfValues;
	public CreateUDFValueServiceCall(final RequestTrackingId trackingId, String endPoint, List<UDFValue> udfValues) {
		super(trackingId, endPoint);
		this.udfValues = udfValues;
	}

	@Override
	protected Holder<List<ObjectId>> command() throws P6ServiceException {
		
		List<ObjectId> objectIds = null;
		try {
			logger.debug("calling createUDFValues with udf values == {}",udfValues);
			objectIds = servicePort.createUDFValues(udfValues);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		}
		return new Holder<List<ObjectId>>(objectIds);
	}


}
