/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.util.CacheManager;
import au.com.wp.corp.p6.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.wsclient.constant.P6WSConstants;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.soap.AbstractSOAPCall;
import au.com.wp.corp.p6.wsclient.soap.SOAPLoggingHandler;
import au.com.wp.corp.p6.wsclient.udftype.IntegrationFault;
import au.com.wp.corp.p6.wsclient.udftype.UDFType;
import au.com.wp.corp.p6.wsclient.udftype.UDFTypeFieldType;
import au.com.wp.corp.p6.wsclient.udftype.UDFTypePortType;
import au.com.wp.corp.p6.wsclient.udftype.UDFTypeService;

/**
 * @author n039126
 * @version 1.0
 */
public class UDFTypeServiceCall extends AbstractSOAPCall<List<UDFType>> {
	private static final Logger logger = LoggerFactory.getLogger(UDFTypeServiceCall.class);

	protected UDFTypePortType servicePort;
	private BindingProvider bp;
	private String endPoint;
	private final String filter;

	public UDFTypeServiceCall(final RequestTrackingId trackingId, final String filter) {
		super(trackingId);
		this.endPoint = P6ReloadablePropertiesReader.getProperty(P6WSConstants.P6_UDF_TYPE_SERVICE_WSDL);
		this.filter = filter;
	}

	@Override
	protected void doBefore() throws P6ServiceException {
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
		servicePort = service.getUDFTypePort();
		bp = (BindingProvider) servicePort;

		Map<String, List<String>> headers = (Map<String, List<String>>) bp.getRequestContext()
				.get("javax.xml.ws.http.request.headers");
		if (headers == null) {
			headers = new HashMap<String, List<String>>();
			bp.getRequestContext().put("javax.xml.ws.http.request.headers", headers);
		}
		logger.debug("WS_COOKIE == " + CacheManager.getWsHeaders().get("WS_COOKIE"));

		headers.put("cookie", CacheManager.getWsHeaders().get("WS_COOKIE"));

		final List<Handler> handlerChain = bp.getBinding().getHandlerChain();
		handlerChain.add(new SOAPLoggingHandler(trackingId));
		bp.getBinding().setHandlerChain(handlerChain);
	}

	@Override
	protected Holder<List<UDFType>> command() throws P6ServiceException {
		logger.info("Calling activity service to update activity.....");
		
		List<UDFTypeFieldType> fields = new ArrayList<>();
		
		fields.add(UDFTypeFieldType.DATA_TYPE);
		fields.add(UDFTypeFieldType.OBJECT_ID);
		fields.add(UDFTypeFieldType.TITLE);
		
		List<UDFType> udfTypes;
		try {
			udfTypes = servicePort.readUDFTypes(fields, filter, null);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		}

		return new Holder<>(udfTypes);
	}

	@Override
	protected void doAfter() {

	}

}
