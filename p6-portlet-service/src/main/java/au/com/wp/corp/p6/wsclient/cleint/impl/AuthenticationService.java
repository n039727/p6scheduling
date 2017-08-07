package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import com.sun.xml.ws.client.BindingProviderProperties;

import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.utils.CacheManager;
import au.com.wp.corp.p6.utils.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.wsclient.auth.AuthenticationServicePortType;
import au.com.wp.corp.p6.wsclient.auth.IntegrationFault;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.soap.AbstractSOAPCall;

public class AuthenticationService extends AbstractSOAPCall<Boolean> {
	private static final Logger logger = LoggerFactory.getLogger(ActivityServiceCall.class);
	
	private BindingProvider bp;
	private AuthenticationServicePortType servicePort;
	private List<String> cookieHeaders = null;
	private final String endPoint;
	
	private final String userPrincipal;
	private  final String userCredential;
	private final int p6DBInstance;

	public AuthenticationService(final RequestTrackingId trackingId, final String endPoint , final String userPrincipal, final String userCredential, final int p6DBInstance) {
		super(trackingId);
		this.endPoint = endPoint;
		this.userPrincipal = userPrincipal;
		this.userCredential = userCredential;
		this.p6DBInstance = p6DBInstance;
		
	}

	@Override
	protected void doBefore() throws P6ServiceException {
		URL wsdlURL = null;
		try {
			wsdlURL = new URL(endPoint);
		} catch (MalformedURLException e) {
			throw new P6ServiceException(e);
		}
		au.com.wp.corp.p6.wsclient.auth.AuthenticationService Service = new au.com.wp.corp.p6.wsclient.auth.AuthenticationService(
				wsdlURL,
				new QName("http://xmlns.oracle.com/Primavera/P6/WS/Authentication/V1", "AuthenticationService"));
		servicePort = Service.getAuthenticationServiceSOAP12PortHttp();
		bp = (BindingProvider) servicePort;
		
	}

	@Override
	protected Holder<Boolean> command() throws P6ServiceException {
		boolean status=false;
		
			try {
				status = servicePort.login(userPrincipal, userCredential, p6DBInstance);
			} catch (IntegrationFault e) {
				
				throw new P6ServiceException(e);
				

			}catch (Exception e) {
				
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
		logger.info("WS_COOKIE == "+ CacheManager.getWsHeaders().get("WS_COOKIE"));
	}

}
