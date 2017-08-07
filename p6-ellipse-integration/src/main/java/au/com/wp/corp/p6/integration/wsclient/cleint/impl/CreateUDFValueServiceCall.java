/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.wsclient.cleint.qualifier.CreateUDFValue;
import au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValuesResponse.ObjectId;
import au.com.wp.corp.p6.wsclient.udfvalue.IntegrationFault;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValue;

/**
 * CreatUDFValueServiceCall, webservice client for P6 CreateUDFValueService.
 * 
 * @author n039126
 * @version 1.0
 */
@Component
@CreateUDFValue
@Lazy
public class CreateUDFValueServiceCall extends UDFValueServiceCall<List<ObjectId>> {

	private static final Logger logger = LoggerFactory.getLogger(CreateUDFValueServiceCall.class);

	private List<UDFValue> udfValues;

	public CreateUDFValueServiceCall() throws P6ServiceException {
		super();
	}

	@Override
	protected List<ObjectId> command() throws P6ServiceException {

		List<ObjectId> objectIds = null;
		try {
			logger.debug("calling createUDFValues with udf values == {}", udfValues);
			objectIds = servicePort.createUDFValues(udfValues);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		}finally {
			this.udfValues = null;
		}
		return objectIds;
	}
	
	@Override
	protected List<ObjectId> createUDFValues(List<UDFValue> udfValues) throws P6ServiceException{
		this.udfValues = udfValues;
		List<ObjectId> objectIds =  run();
		return null;
	}

}
