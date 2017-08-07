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
import au.com.wp.corp.p6.integration.wsclient.cleint.qualifier.DeleteUDFValue;

/**
 * @author n039126
 *
 */
@Component
@DeleteUDFValue
@Lazy
public class DeleteUDFValueServiceCall extends UDFValueServiceCall<Boolean> {
	private static final Logger logger1 = LoggerFactory.getLogger(DeleteUDFValueServiceCall.class);
	

	private List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds;
	public DeleteUDFValueServiceCall() throws P6ServiceException{
		super();
	}

	@Override
	protected Boolean command() throws P6ServiceException {
		Boolean returnVal = Boolean.FALSE;
		
		try {
			logger1.debug("calling deleteUDFValues with udf values == {}",objectIds);
			
			returnVal = servicePort.deleteUDFValues(objectIds);
			
		} catch (au.com.wp.corp.p6.wsclient.udfvalue.IntegrationFault e) {
			throw new P6ServiceException(e);
		} finally {
			this.objectIds = null;
		}
		return returnVal;
	}

	@Override
	public boolean deleteUDFValues(List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds) throws P6ServiceException{
		this.objectIds = objectIds;
		return run();
	}

}
