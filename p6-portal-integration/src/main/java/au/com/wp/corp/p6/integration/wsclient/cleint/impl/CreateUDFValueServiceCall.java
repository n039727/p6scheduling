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
import au.com.wp.corp.p6.wsclient.udftype.UDFType;
import au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValuesResponse.ObjectId;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValue;

/**
 * @author n039126
 *
 */
public class CreateUDFValueServiceCall extends UDFValueServiceCall<List<ObjectId>> {
	private static final Logger logger = LoggerFactory.getLogger(CreateUDFValueServiceCall.class);
	
	private List<ExecutionPackageCreateRequest> executionPackageCreateRequest;
	public CreateUDFValueServiceCall(final RequestTrackingId trackingId, List<ExecutionPackageCreateRequest> executionPackageCreateRequest) {
		super(trackingId);
		this.executionPackageCreateRequest = executionPackageCreateRequest;
	}

	@Override
	protected Holder<List<ObjectId>> command() throws P6ServiceException {

		List<UDFValue> createUDFValues = new ArrayList<UDFValue>();
		int typeObjectId = readUDFTypeForExecutionPackage();
		for (ExecutionPackageCreateRequest request : executionPackageCreateRequest) {
			UDFValue udfValue = new UDFValue();
			udfValue.setForeignObjectId(request.getForeignObjectId());
			udfValue.setText(request.getText());
			udfValue.setUDFTypeDataType(request.getUdfTypeDataType());
			udfValue.setUDFTypeObjectId(typeObjectId);
			udfValue.setUDFTypeSubjectArea(request.getUdfTypeSubjectArea());
			udfValue.setUDFTypeTitle(request.getUdfTypeTitle());
			createUDFValues.add(udfValue);
		}

		List<ObjectId> objectIds = null;
		try {
			logger.debug("calling createUDFValues with udf values == {}", createUDFValues);
			objectIds = servicePort.createUDFValues(createUDFValues);
		} catch (au.com.wp.corp.p6.wsclient.udfvalue.IntegrationFault e) {
			// TODO Auto-generated catch block
			logger.error("IntegrationFault occured>>>{}", e);
		}
		return new Holder<List<ObjectId>>(objectIds);
	}
	
	private int readUDFTypeForExecutionPackage() throws P6ServiceException {
		logger.info("Reading UDF type details from P6 ..");
	
		StringBuilder filter = new StringBuilder();
		filter.append("SubjectArea='Activity' and Title = 'Execution Grouping'");

		UDFTypeServiceCall udfTypeServiceCall = new UDFTypeServiceCall(trackingId, filter.toString());
		Holder<List<UDFType>> udfTypes = udfTypeServiceCall.run();
		return udfTypes.value.get(0).getObjectId();
	}
}
