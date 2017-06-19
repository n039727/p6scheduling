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

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.utils.CacheManager;
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.activity.ActivityFieldType;
import au.com.wp.corp.p6.wsclient.activity.ActivityPortType;
import au.com.wp.corp.p6.wsclient.activity.ActivityService;
import au.com.wp.corp.p6.wsclient.activity.IntegrationFault;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.soap.AbstractSOAPCall;
import au.com.wp.corp.p6.wsclient.soap.SOAPLoggingHandler;

/**
 * @author n039126
 *
 */
public class ActivityServiceCall extends AbstractSOAPCall<List<Activity>> {
	private static final Logger logger = LoggerFactory.getLogger(ActivityServiceCall.class);
	
	private BindingProvider bp;
	private ActivityPortType servicePort;
	private String endPoint; 
	private String filter;
	
	public ActivityServiceCall(final RequestTrackingId trackingId, String endPoint, String filter) {
		super(trackingId);
		this.filter = filter;
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
		
		ActivityService Service = new ActivityService(
				wsdlURL,
				new QName("http://xmlns.oracle.com/Primavera/P6/WS/Activity/V1", "ActivityService"));
		servicePort = Service.getActivityPort();
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
	protected Holder<List<Activity>> command() throws P6ServiceException {
		
		List<ActivityFieldType> fields = new ArrayList<>();
		
		fields.add(ActivityFieldType.PLANNED_START_DATE );
		fields.add(ActivityFieldType.PLANNED_FINISH_DATE );
		fields.add(ActivityFieldType.ID);
		fields.add(ActivityFieldType.OBJECT_ID);
		fields.add(ActivityFieldType.NAME);
		fields.add(ActivityFieldType.PRIMARY_RESOURCE_OBJECT_ID);
		fields.add(ActivityFieldType.PRIMARY_RESOURCE_NAME);
		fields.add(ActivityFieldType.PROJECT_OBJECT_ID);
		fields.add(ActivityFieldType.PRIMARY_RESOURCE_ID);
				
		List<Activity> activities;
		try {
			activities = servicePort.readActivities(fields, filter, null);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		}
		
		return new Holder<>(activities);
	}

	@Override
	protected void doAfter() {
		// TODO Auto-generated method stub

	}

}
