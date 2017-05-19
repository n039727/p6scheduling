/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.util.List;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.dto.ExecutionPackageCreateRequest;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValues;
import au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValuesResponse;
import au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValuesResponse.ObjectId;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValue;

/**
 * @author n039126
 *
 */
public class CreateUDFValueServiceCall extends UDFValueServiceCall<List<ObjectId>> {
	private static final Logger logger = LoggerFactory.getLogger(CreateUDFValueServiceCall.class);
	
	private List<ExecutionPackageCreateRequest> executionPackageCreateRequest;
	public CreateUDFValueServiceCall(final RequestTrackingId trackingId, String endPoint, List<ExecutionPackageCreateRequest> executionPackageCreateRequest) {
		super(trackingId, endPoint);
		this.executionPackageCreateRequest = executionPackageCreateRequest;
	}

	@Override
	protected Holder<List<ObjectId>> command() throws P6ServiceException {
		CreateUDFValuesResponse createUDFValuesResponse = new CreateUDFValuesResponse();
		
		CreateUDFValues createUDFValues = new CreateUDFValues();
		
		for (ExecutionPackageCreateRequest request : executionPackageCreateRequest) {
			UDFValue udfValue = new UDFValue();
			udfValue.setForeignObjectId(request.getForeignObjectId());
			udfValue.setText(request.getText());
			udfValue.setUDFTypeDataType(request.getUdfTypeDataType());
			udfValue.setUDFTypeObjectId(request.getUdfTypeObjectId());
			udfValue.setUDFTypeSubjectArea(request.getUdfTypeSubjectArea());
			udfValue.setUDFTypeTitle(request.getUdfTypeTitle());
			createUDFValues.getUDFValue().add(udfValue);
		}
		
		
		List<ObjectId> objectIds = null;
		try {
			logger.debug("calling createUDFValues with udf values == {}",createUDFValues.getUDFValue());
			objectIds = servicePort.createUDFValues(createUDFValues.getUDFValue());
		} catch (au.com.wp.corp.p6.wsclient.udfvalue.IntegrationFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createUDFValuesResponse.getObjectId().addAll(objectIds);
		return new Holder<List<ObjectId>>(objectIds);
	}


}
