package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.net.MalformedURLException;
import java.net.URL;
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
import au.com.wp.corp.p6.wsclient.auth.AuthenticationServicePortType;
import au.com.wp.corp.p6.wsclient.auth.IntegrationFault;
import au.com.wp.corp.p6.wsclient.constant.P6EllipseWSConstants;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.soap.AbstractSOAPCall;
import au.com.wp.corp.p6.wsclient.soap.SOAPLoggingHandler;

public class AuthenticationService extends AbstractSOAPCall<Boolean> {
	private static final Logger logger1 = LoggerFactory.getLogger(ActivityServiceCall.class);
	
	private BindingProvider bp;
	private AuthenticationServicePortType servicePort;
	private List<String> cookieHeaders = null;
	private final String endPoint;
	
	private final String userPrincipal;
	private  final String userCredential;
	private final String p6DBInstance;

	public AuthenticationService(final RequestTrackingId trackingId) {
		super(trackingId);
		this.endPoint = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_AUTH_SERVICE_WSDL);
		this.userPrincipal = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_USER_PRINCIPAL);
		this.userCredential = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_USER_CREDENTIAL);
		this.p6DBInstance = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_DB_INSTANCE);
		
	}

	@Override
	protected void doBefore() throws P6ServiceException {
		
		if ( null == endPoint || null == userPrincipal || null == userCredential || null == p6DBInstance){
			throw new P6ServiceException("Authentication Service end point or user principal or user credential or DB instance is null ");
		}
		
		URL wsdlURL = null;
		try {
			wsdlURL = new URL(endPoint);
		} catch (MalformedURLException e) {
			throw new P6ServiceException(e);
		}
		au.com.wp.corp.p6.wsclient.auth.AuthenticationService authService = new au.com.wp.corp.p6.wsclient.auth.AuthenticationService(
				wsdlURL);
		servicePort = authService.getAuthenticationServiceSOAP12PortHttp();
		bp = (BindingProvider) servicePort;
	
		final List<Handler> handlerChain = bp.getBinding().getHandlerChain();
        handlerChain.add(new SOAPLoggingHandler(trackingId));
        bp.getBinding().setHandlerChain(handlerChain);
	}

	@Override
	protected Holder<Boolean> command() throws P6ServiceException {
		boolean status;
		try {
			final int dbInstance = Integer.parseInt(p6DBInstance);
			status = servicePort.login(userPrincipal, userCredential, dbInstance);
		} catch (IntegrationFault | NumberFormatException e) {
			throw new P6ServiceException(e);
		}
		final Holder<Boolean> holder = new Holder<>(status);
		final Map<String, List<String>> responseHeaders = (Map<String, List<String>>) bp.getResponseContext()
				.get("javax.xml.ws.http.response.headers");
		cookieHeaders = responseHeaders.get("Set-Cookie");
		return holder;
	}

	@Override
	protected void doAfter() {
		CacheManager.getWsHeaders().put("WS_COOKIE", cookieHeaders);
		logger1.info("WS_COOKIE == "+ CacheManager.getWsHeaders().get("WS_COOKIE"));
	}

}
