/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.impl;

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

import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.utils.CacheManager;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.soap.AbstractSOAPCall;
import au.com.wp.corp.p6.wsclient.soap.SOAPLoggingHandler;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValuePortType;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValueService;

/**
 * @author n039126
 *
 */
public abstract class UDFValueServiceCall<T> extends AbstractSOAPCall<T>{
	private static final Logger logger = LoggerFactory.getLogger(UDFValueServiceCall.class);
	
	private BindingProvider bp;
	protected UDFValuePortType servicePort;
	private String endPoint; 
	public UDFValueServiceCall(final RequestTrackingId trackingId, String endPoint) {
		super(trackingId);
		this.endPoint = endPoint;
	}

	@Override
	protected void doBefore() throws P6ServiceException {
		URL wsdlURL = null;
		try {
			wsdlURL = new URL(endPoint);
		} catch (MalformedURLException e) {
			throw new P6ServiceException(e);
		}
		
		UDFValueService Service = new UDFValueService(
				wsdlURL,
				new QName("http://xmlns.oracle.com/Primavera/P6/WS/UDFValue/V1", "UDFValueService"));
		servicePort = Service.getUDFValuePort();
		bp = (BindingProvider) servicePort;
		Map<String, List<String>> headers = (Map<String, List<String>>) bp.getRequestContext()
				.get("javax.xml.ws.http.request.headers");
		if (headers == null) {
			headers = new HashMap<String, List<String>>();
			bp.getRequestContext().put("javax.xml.ws.http.request.headers", headers);

		}
		logger.debug("WS_COOKIE == "+ CacheManager.getWsHeaders().get("WS_COOKIE"));
		
		headers.put("cookie", CacheManager.getWsHeaders().get("WS_COOKIE"));
		
		final List<Handler> handlerChain = bp.getBinding().getHandlerChain();
        handlerChain.add(new SOAPLoggingHandler(trackingId));
        bp.getBinding().setHandlerChain(handlerChain);


	}

	

	@Override
	protected void doAfter() {
		// TODO Auto-generated method stub

	}

}
