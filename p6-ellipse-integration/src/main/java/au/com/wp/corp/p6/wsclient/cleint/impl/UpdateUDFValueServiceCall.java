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
import au.com.wp.corp.p6.wsclient.udfvalue.IntegrationFault;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValue;

/**
 * UpdateUDFValueServiceCall, webservice client for P6 CreateUDFValueService.
 * 
 * @author n039126
 * @version 1.0
 */
public class UpdateUDFValueServiceCall extends UDFValueServiceCall<Boolean> {
	private static final Logger logger1 = LoggerFactory.getLogger(UpdateUDFValueServiceCall.class);

	private final List<UDFValue> udfValues;

	public UpdateUDFValueServiceCall(final RequestTrackingId trackingId, List<UDFValue> udfValues) {
		super(trackingId);
		this.udfValues = udfValues;
	}

	@Override
	protected Holder<Boolean> command() throws P6ServiceException {

		Boolean status = null;
		try {
			logger1.debug("calling update UDFValues with udf values == {}", udfValues);
			status = servicePort.updateUDFValues(udfValues);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		}
		return new Holder<>(status);
	}

}
