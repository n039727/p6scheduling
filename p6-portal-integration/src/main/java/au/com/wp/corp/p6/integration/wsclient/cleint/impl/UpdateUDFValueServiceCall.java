/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.integration.dto.ExecutionPackageCreateRequest;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValue;

/**
 * @author n039126
 *
 */
public class UpdateUDFValueServiceCall extends UDFValueServiceCall<Boolean> {
	private static final Logger logger = LoggerFactory.getLogger(UpdateUDFValueServiceCall.class);
	
	private List<ExecutionPackageCreateRequest> executionPackageCreateRequest;
	public UpdateUDFValueServiceCall(final RequestTrackingId trackingId, String endPoint, List<ExecutionPackageCreateRequest> executionPackageCreateRequest) {
		super(trackingId);
		this.executionPackageCreateRequest = executionPackageCreateRequest;
	}

	@Override
	protected Holder<Boolean> command() throws P6ServiceException {
		List<UDFValue> updateUDFValues = new ArrayList<UDFValue>();

		for (ExecutionPackageCreateRequest request : executionPackageCreateRequest) {
			UDFValue udfValue = new UDFValue();
			udfValue.setForeignObjectId(request.getForeignObjectId());
			udfValue.setText(request.getText());
			udfValue.setUDFTypeDataType(request.getUdfTypeDataType());
			udfValue.setUDFTypeObjectId(request.getUdfTypeObjectId());
			udfValue.setUDFTypeSubjectArea(request.getUdfTypeSubjectArea());
			udfValue.setUDFTypeTitle(request.getUdfTypeTitle());
			updateUDFValues.add(udfValue);
		}


		Boolean returnVal = Boolean.FALSE;
		try {
			logger.debug("calling createUDFValues with udf values == {}", updateUDFValues);
			if(!updateUDFValues.isEmpty()){
				returnVal = servicePort.updateUDFValues(updateUDFValues);
			}
		} catch (au.com.wp.corp.p6.wsclient.udfvalue.IntegrationFault e) {
			throw new P6ServiceException(e);
		}
		return new Holder<Boolean>(returnVal);
	}


}
