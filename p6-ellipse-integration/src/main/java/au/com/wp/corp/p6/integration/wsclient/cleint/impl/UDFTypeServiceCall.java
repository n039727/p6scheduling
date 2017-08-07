/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.integration.wsclient.constant.P6EllipseWSConstants;
import au.com.wp.corp.p6.integration.wsclient.soap.AbstractSOAPCall;
import au.com.wp.corp.p6.integration.wsclient.soap.SOAPLoggingHandler;
import au.com.wp.corp.p6.wsclient.udftype.IntegrationFault;
import au.com.wp.corp.p6.wsclient.udftype.UDFType;
import au.com.wp.corp.p6.wsclient.udftype.UDFTypeFieldType;
import au.com.wp.corp.p6.wsclient.udftype.UDFTypePortType;
import au.com.wp.corp.p6.wsclient.udftype.UDFTypeService;

/**
 * @author n039126
 * @version 1.0
 */
@Component
@Lazy
public class UDFTypeServiceCall extends AbstractSOAPCall<List<UDFType>> {
	private static final Logger logger1 = LoggerFactory.getLogger(UDFTypeServiceCall.class);

	protected UDFTypePortType servicePort;
	private String filter;
	protected BindingProvider bp;
	protected SOAPLoggingHandler soapHandler;
	
	public UDFTypeServiceCall() throws P6ServiceException{
		super();
		String endPoint = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_UDF_TYPE_SERVICE_WSDL);
		if ( null == endPoint ){
			throw new P6ServiceException("UDF Tpe Service end point is null ");
		}
		URL wsdlURL = null;
		try {
			wsdlURL = new URL(endPoint);
		} catch (MalformedURLException e) {
			throw new P6ServiceException(e);
		}

		UDFTypeService service = new UDFTypeService(wsdlURL);
		this.servicePort = service.getUDFTypePort();
		this.bp = (BindingProvider) servicePort;
		this.soapHandler = new SOAPLoggingHandler();

		final List<Handler> handlerChain = bp.getBinding().getHandlerChain();
		handlerChain.add(new SOAPLoggingHandler());
		bp.getBinding().setHandlerChain(handlerChain);
	}

	@Override
	protected void doBefore() throws P6ServiceException {

		Map<String, List<String>> headers = (Map<String, List<String>>) bp.getRequestContext()
				.get("javax.xml.ws.http.request.headers");
		if (headers == null) {
			headers = new HashMap<>();
			bp.getRequestContext().put("javax.xml.ws.http.request.headers", headers);
		}
		logger1.debug("WS_COOKIE == " + CacheManager.getWsHeaders().get("WS_COOKIE"));

		headers.put("cookie", CacheManager.getWsHeaders().get("WS_COOKIE"));
	}

	@Override
	protected List<UDFType> command() throws P6ServiceException {
		logger1.info("Calling activity service to update activity.....");
		
		List<UDFTypeFieldType> fields = new ArrayList<>();
		
		fields.add(UDFTypeFieldType.DATA_TYPE);
		fields.add(UDFTypeFieldType.OBJECT_ID);
		fields.add(UDFTypeFieldType.TITLE);
		
		List<UDFType> udfTypes;
		try {
			udfTypes = servicePort.readUDFTypes(fields, filter, null);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		} finally {
			this.filter = null;
		}

		return udfTypes;
	}
	
	public List<UDFType> readUDFTypes ( final String filter) throws P6ServiceException{
		this.filter = filter;
		return run();
	}

	@Override
	protected void doAfter() {

	}

}
