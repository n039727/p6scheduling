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

import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.utils.CacheManager;
import au.com.wp.corp.p6.wsclient.auth.AuthenticationServicePortType;
import au.com.wp.corp.p6.wsclient.auth.IntegrationFault;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.soap.AbstractSOAPCall;

public class AuthenticationService extends AbstractSOAPCall<Boolean> {
	private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);
	
	private BindingProvider bp;
	private AuthenticationServicePortType servicePort;
	private static List<String> cookieHeaders = null;
	private final String endPoint;

	public AuthenticationService(final RequestTrackingId trackingId, String endPoint) {
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
		au.com.wp.corp.p6.wsclient.auth.AuthenticationService Service = new au.com.wp.corp.p6.wsclient.auth.AuthenticationService(
				wsdlURL,
				new QName("http://xmlns.oracle.com/Primavera/P6/WS/Authentication/V1", "AuthenticationService"));
		servicePort = Service.getAuthenticationServiceSOAP12PortHttp();
		bp = (BindingProvider) servicePort;
	}

	@Override
	protected Holder<Boolean> command() throws P6ServiceException {
		boolean status;
		try {
			status = servicePort.login("svc_P6_SYS_USER_NP", "QChihIC4s", 1);
		} catch (IntegrationFault e) {
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
