/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.wsclient.cleint.qualifier.UpdateUDFValue;
import au.com.wp.corp.p6.wsclient.udfvalue.IntegrationFault;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValue;

/**
 * UpdateUDFValueServiceCall, webservice client for P6 CreateUDFValueService.
 * 
 * @author n039126
 * @version 1.0
 */
@Component
@UpdateUDFValue
public class UpdateUDFValueServiceCall extends UDFValueServiceCall<Boolean> {
	private static final Logger logger1 = LoggerFactory.getLogger(UpdateUDFValueServiceCall.class);

	private List<UDFValue> udfValues;
	
	public UpdateUDFValueServiceCall() throws P6ServiceException {
		super();
	}

	@Override
	protected Boolean command() throws P6ServiceException {

		Boolean status = null;
		try {
			logger1.debug("calling update UDFValues with udf values == {}", udfValues);
			status = servicePort.updateUDFValues(udfValues);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		} finally {
			this.udfValues = null;
		}
		return status;
	}

	@Override
	protected boolean updateUDFValues(List<UDFValue> udfValues) throws P6ServiceException{
		this.udfValues = udfValues;
		boolean status = run();
		return status;
	}
}
