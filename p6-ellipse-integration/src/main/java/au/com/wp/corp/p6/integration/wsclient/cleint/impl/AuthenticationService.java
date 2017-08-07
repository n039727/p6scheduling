package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.net.MalformedURLException;
import java.net.URL;
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
import au.com.wp.corp.p6.wsclient.auth.AuthenticationServicePortType;
import au.com.wp.corp.p6.wsclient.auth.IntegrationFault;

@Component
@Lazy
public class AuthenticationService extends AbstractSOAPCall<Boolean> {
	private static final Logger logger1 = LoggerFactory.getLogger(ActivityServiceCall.class);
	
	private BindingProvider bp;
	private AuthenticationServicePortType servicePort;
	private List<String> cookieHeaders = null;
	private SOAPLoggingHandler soapHandler;
	public AuthenticationService() throws P6ServiceException{
		super();
		String endPoint = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_AUTH_SERVICE_WSDL);
		if ( null == endPoint ){
			throw new P6ServiceException("Authentication Service end point is null ");
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
		this.soapHandler = new SOAPLoggingHandler();
		final List<Handler> handlerChain = bp.getBinding().getHandlerChain();
        handlerChain.add(soapHandler);
        bp.getBinding().setHandlerChain(handlerChain);
        logger1.info("AuthenticationService object creating......");
	}

	@Override
	protected void doBefore() throws P6ServiceException {
	}

	@Override
	protected Boolean command() throws P6ServiceException {
		String userPrincipal = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_USER_PRINCIPAL);
		String userCredential = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_USER_CREDENTIAL);
		String p6DBInstance = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_DB_INSTANCE);
		
		if ( null == userPrincipal || null == userCredential || null == p6DBInstance){
			throw new P6ServiceException("Authentication Service user principal or user credential or DB instance is null ");
		}
		boolean status;
		try {
			final int dbInstance = Integer.parseInt(p6DBInstance);
			status = servicePort.login(userPrincipal, userCredential, dbInstance);
		} catch (IntegrationFault | NumberFormatException e) {
			throw new P6ServiceException(e);
		}
		final Map<String, List<String>> responseHeaders = (Map<String, List<String>>) bp.getResponseContext()
				.get("javax.xml.ws.http.response.headers");
		cookieHeaders = responseHeaders.get("Set-Cookie");
		return status;
	}

	@Override
	protected void doAfter() {
		CacheManager.getWsHeaders().put("WS_COOKIE", cookieHeaders);
		logger1.info("WS_COOKIE == "+ CacheManager.getWsHeaders().get("WS_COOKIE"));
	}

}
