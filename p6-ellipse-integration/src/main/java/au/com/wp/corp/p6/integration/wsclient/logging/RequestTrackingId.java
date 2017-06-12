package au.com.wp.corp.p6.integration.wsclient.logging;

import java.util.UUID;

import javax.servlet.ServletRequest;

public class RequestTrackingId {
	
	public static final String LOG_ID = "requestTrackingId";
	private final UUID randomUUID;
	
	
	public RequestTrackingId() {
		randomUUID = UUID.randomUUID();
	}
	
	public RequestTrackingId(ServletRequest servletRequest) {
		randomUUID = UUID.randomUUID();
		servletRequest.setAttribute(RequestTrackingId.LOG_ID, this);
	}
	
	public static RequestTrackingId fromRequest(ServletRequest servletRequest) {		
		RequestTrackingId trackingId = (RequestTrackingId) servletRequest.getAttribute(LOG_ID);
		if (trackingId == null) {
			throw new IllegalStateException("Missing request parameter " + LOG_ID + "; register RequestTrackingFilter with web.xml");
		}
		return trackingId;
	}

	@Override
	public String toString() {
		return randomUUID.toString() + " #";
	}
	
	
	
}
