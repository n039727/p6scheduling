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

/**
 * @author n039126
 *
 */
public class DeleteUDFValueServiceCall extends UDFValueServiceCall<Boolean> {
	private static final Logger logger = LoggerFactory.getLogger(DeleteUDFValueServiceCall.class);
	

	private List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds;
	public DeleteUDFValueServiceCall(final RequestTrackingId trackingId, final List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds) {
		super(trackingId);
		this.objectIds = objectIds;
	}

	@Override
	protected Holder<Boolean> command() throws P6ServiceException {
		Boolean returnVal = Boolean.FALSE;
		
		try {
			logger.debug("calling deleteUDFValues with udf values == {}",objectIds);
			
			returnVal = servicePort.deleteUDFValues(objectIds);
			
		} catch (au.com.wp.corp.p6.wsclient.udfvalue.IntegrationFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Holder<>(returnVal);
	}


}
