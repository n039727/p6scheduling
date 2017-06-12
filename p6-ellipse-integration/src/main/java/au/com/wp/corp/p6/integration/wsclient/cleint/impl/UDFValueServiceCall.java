/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.integration.wsclient.constant.P6EllipseWSConstants;
import au.com.wp.corp.p6.integration.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.integration.wsclient.soap.AbstractSOAPCall;
import au.com.wp.corp.p6.integration.wsclient.soap.SOAPLoggingHandler;
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

	private BindingProvider bp;
	protected UDFValuePortType servicePort;
	private String endPoint;

	public UDFValueServiceCall(final RequestTrackingId trackingId) {
		super(trackingId);
		this.endPoint = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_UDF_SERVICE_WSDL);
	}

	@Override
	protected void doBefore() throws P6ServiceException {
		if ( null == endPoint ){
			throw new P6ServiceException("UDF Value Service end point is null ");
		}
		URL wsdlURL = null;
		try {
			wsdlURL = new URL(endPoint);
		} catch (MalformedURLException e) {
			throw new P6ServiceException(e);
		}

		final UDFValueService udfValueService = new UDFValueService(wsdlURL,
				new QName("http://xmlns.oracle.com/Primavera/P6/WS/UDFValue/V1", "UDFValueService"));
		servicePort = udfValueService.getUDFValuePort();
		bp = (BindingProvider) servicePort;
		@SuppressWarnings("unchecked")
		Map<String, List<String>> headers = (Map<String, List<String>>) bp.getRequestContext()
				.get("javax.xml.ws.http.request.headers");
		if (headers == null) {
			headers = new HashMap<>();
			bp.getRequestContext().put("javax.xml.ws.http.request.headers", headers);

		}
		log.debug("WS_COOKIE == " + CacheManager.getWsHeaders().get("WS_COOKIE"));

		headers.put("cookie", CacheManager.getWsHeaders().get("WS_COOKIE"));

		final List<Handler> handlerChain = bp.getBinding().getHandlerChain();
		handlerChain.add(new SOAPLoggingHandler(trackingId));
		bp.getBinding().setHandlerChain(handlerChain);

	}

	@Override
	protected void doAfter() {

	}

}
