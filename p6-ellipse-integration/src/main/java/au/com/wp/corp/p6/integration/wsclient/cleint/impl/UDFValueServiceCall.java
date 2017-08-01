/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.integration.wsclient.constant.P6EllipseWSConstants;
import au.com.wp.corp.p6.integration.wsclient.soap.AbstractSOAPCall;
import au.com.wp.corp.p6.integration.wsclient.soap.SOAPLoggingHandler;
import au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValuesResponse.ObjectId;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValue;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValuePortType;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValueService;

/**
 * UDFValueServiceCall is abstract client to provide service to call web service
 * 
 * 
 * @author n039126
 * @version 1.0
 *
 */
public abstract class UDFValueServiceCall<T> extends AbstractSOAPCall<T> {
	private static final Logger log = LoggerFactory.getLogger(UDFValueServiceCall.class);

	protected UDFValuePortType servicePort;
	protected BindingProvider bp;
	protected SOAPLoggingHandler soapHandler;
	
	public UDFValueServiceCall() throws P6ServiceException{
		super();
		String endPoint = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_UDF_SERVICE_WSDL);
		if ( null == endPoint ){
			throw new P6ServiceException("UDF Value Service end point is null ");
		}
		URL wsdlURL = null;
		try {
			wsdlURL = new URL(endPoint);
		} catch (MalformedURLException e) {
			throw new P6ServiceException(e);
		}

		final UDFValueService udfValueService = new UDFValueService(wsdlURL);
		this.servicePort = udfValueService.getUDFValuePort();
		this.bp = (BindingProvider) servicePort;
		this.soapHandler = new SOAPLoggingHandler();

		final List<Handler> handlerChain = bp.getBinding().getHandlerChain();
		handlerChain.add(soapHandler);
		this.bp.getBinding().setHandlerChain(handlerChain);
	}

	@Override
	protected void doBefore() throws P6ServiceException {
		@SuppressWarnings("unchecked")
		Map<String, List<String>> headers = (Map<String, List<String>>) bp.getRequestContext()
				.get("javax.xml.ws.http.request.headers");
		if (headers == null) {
			headers = new HashMap<>();
			bp.getRequestContext().put("javax.xml.ws.http.request.headers", headers);

		}
		log.debug("WS_COOKIE == " + CacheManager.getWsHeaders().get("WS_COOKIE"));

		headers.put("cookie", CacheManager.getWsHeaders().get("WS_COOKIE"));

	}

	@Override
	protected void doAfter() {

	}

	protected List<ObjectId> createUDFValues(List<UDFValue> udfValues) throws P6ServiceException{
		return null;
	}
	
	protected List<UDFValue> readUDFValues(final String filter, final String orderBy) throws P6ServiceException{
		return null;
	}

	protected boolean deleteUDFValues(List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds) throws P6ServiceException{
		return Boolean.FALSE;
	}

	protected boolean updateUDFValues(List<UDFValue> udfValues) throws P6ServiceException{
		return Boolean.FALSE;
	}
	
	
	
}
