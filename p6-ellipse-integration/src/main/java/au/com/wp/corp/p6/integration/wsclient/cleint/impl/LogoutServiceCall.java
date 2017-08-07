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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.integration.wsclient.constant.P6EllipseWSConstants;
import au.com.wp.corp.p6.integration.wsclient.soap.AbstractSOAPCall;
import au.com.wp.corp.p6.integration.wsclient.soap.SOAPLoggingHandler;
import au.com.wp.corp.p6.wsclient.auth.AuthenticationService;
import au.com.wp.corp.p6.wsclient.auth.AuthenticationServicePortType;
import au.com.wp.corp.p6.wsclient.auth.LogoutResponse;

@Component
@Lazy
public class LogoutServiceCall extends AbstractSOAPCall<LogoutResponse> {
	private static final Logger logger1 = LoggerFactory.getLogger(ActivityServiceCall.class);

	private BindingProvider bp;
	private AuthenticationServicePortType servicePort;
	private SOAPLoggingHandler soapHandler;
	public LogoutServiceCall() throws P6ServiceException{
		super();
		String endPoint = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_AUTH_SERVICE_WSDL);
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
		this.soapHandler = new SOAPLoggingHandler();
		final List<Handler> handlerChain = bp.getBinding().getHandlerChain();
		handlerChain.add(soapHandler);
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
		logger1.debug("Logout with WS_COOKIE == {}", CacheManager.getWsHeaders().get("WS_COOKIE"));
		headers.put("cookie", CacheManager.getWsHeaders().get("WS_COOKIE"));
	}

	@Override
	protected LogoutResponse command() throws P6ServiceException {
		LogoutResponse status;
		try {
			status = servicePort.logout(null);
		} catch (Exception e) {
			throw new P6ServiceException(e);
		}
		return status;
	}
	
	
	public boolean logout () throws P6ServiceException {
		return run().isReturn();
	}

	@Override
	protected void doAfter() {
	}

}
