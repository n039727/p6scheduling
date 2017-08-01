/**
 * 
 */
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

import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.integration.wsclient.constant.P6EllipseWSConstants;
import au.com.wp.corp.p6.integration.wsclient.soap.AbstractSOAPCall;
import au.com.wp.corp.p6.integration.wsclient.soap.SOAPLoggingHandler;
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.activity.ActivityPortType;
import au.com.wp.corp.p6.wsclient.activity.ActivityService;

/**
 * @author n039126
 *
 */
public abstract class ActivityServiceCall<T> extends AbstractSOAPCall<T> {
	private static final Logger log = LoggerFactory.getLogger(ActivityServiceCall.class);

	protected ActivityPortType servicePort;
	private BindingProvider bp;
	
	private SOAPLoggingHandler soapHandler;

	public ActivityServiceCall() throws P6ServiceException{
		super();
		String endPoint = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_ACTIVITY_SERVICE_WSDL);
		if (null == endPoint) {
			throw new P6ServiceException("Activity Service end point is null ");
		}
		URL wsdlURL = null;
		try {
			wsdlURL = new URL(endPoint);
		} catch (MalformedURLException e) {
			throw new P6ServiceException(e);
		}

		ActivityService service = new ActivityService(wsdlURL);
		this.servicePort = service.getActivityPort();
		this.bp = (BindingProvider) servicePort;
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
			headers = new HashMap<String, List<String>>();
			bp.getRequestContext().put("javax.xml.ws.http.request.headers", headers);
		}
		log.debug("WS_COOKIE == {}", CacheManager.getWsHeaders().get("WS_COOKIE"));

		headers.put("cookie", CacheManager.getWsHeaders().get("WS_COOKIE"));
	}

	@Override
	protected void doAfter() {

	}

	protected List<Integer> createActivities(List<Activity> activities) throws P6ServiceException{
		return null;
	}

	protected List<Activity> readActivities(final String filter) throws P6ServiceException{
		return null;
	}
	
	
	protected boolean deleteActivities(List<Integer> activitieIds) throws P6ServiceException{
		return Boolean.FALSE;
	}

	protected boolean updateActivities(List<Activity> activities) throws P6ServiceException{
		return Boolean.FALSE;
	}

}
