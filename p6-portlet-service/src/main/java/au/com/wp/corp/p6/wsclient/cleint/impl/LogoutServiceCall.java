package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.utils.CacheManager;
import au.com.wp.corp.p6.utils.P6EllipseWSConstants;
import au.com.wp.corp.p6.utils.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.wsclient.auth.AuthenticationService;
import au.com.wp.corp.p6.wsclient.auth.AuthenticationServicePortType;
import au.com.wp.corp.p6.wsclient.auth.LogoutResponse;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.soap.AbstractSOAPCall;
import au.com.wp.corp.p6.wsclient.soap.SOAPLoggingHandler;

public class LogoutServiceCall extends AbstractSOAPCall<LogoutResponse> {
	private static final Logger logger1 = LoggerFactory.getLogger(LogoutServiceCall.class);

	private BindingProvider bp;
	private AuthenticationServicePortType servicePort;
	private final String endPoint;

	public LogoutServiceCall(final RequestTrackingId trackingId) {
		super(trackingId);
		this.endPoint = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_AUTH_SERVICE_WSDL);
	}

	@Override
	protected void doBefore() throws P6ServiceException {

		if (null == endPoint) {
			throw new P6ServiceException("Authentication Service end point is null ");
		}

		URL wsdlURL = null;
		try {
			wsdlURL = new URL(endPoint);
		} catch (MalformedURLException e) {
			throw new P6ServiceException(e);
		}
		AuthenticationService authService = new AuthenticationService(wsdlURL);
		servicePort = authService.getAuthenticationServiceSOAP12PortHttp();
		bp = (BindingProvider) servicePort;

		Map<String, List<String>> headers = (Map<String, List<String>>) bp.getRequestContext()
				.get("javax.xml.ws.http.request.headers");
		if (headers == null) {
			headers = new HashMap<>();
			bp.getRequestContext().put("javax.xml.ws.http.request.headers", headers);

		}
		logger1.debug("Logout with WS_COOKIE == {}", CacheManager.getWsHeaders().get("WS_COOKIE"));
		headers.put("cookie", CacheManager.getWsHeaders().get("WS_COOKIE"));
		final List<Handler> handlerChain = bp.getBinding().getHandlerChain();
		handlerChain.add(new SOAPLoggingHandler(trackingId));
		bp.getBinding().setHandlerChain(handlerChain);
	}

	@Override
	protected Holder<LogoutResponse> command() throws P6ServiceException {
		LogoutResponse status;
		try {
			status = servicePort.logout(null);
			CacheManager.getWsHeaders().clear();
		} catch (Exception e) {
			throw new P6ServiceException(e);
		}
		return new Holder<>(status);
	}

	@Override
	protected void doAfter() {
	}

}
