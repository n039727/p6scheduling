/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.integration.wsclient.constant.P6EllipseWSConstants;
import au.com.wp.corp.p6.integration.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.integration.wsclient.soap.AbstractSOAPCall;
import au.com.wp.corp.p6.integration.wsclient.soap.SOAPLoggingHandler;
import au.com.wp.corp.p6.wsclient.project.IntegrationFault;
import au.com.wp.corp.p6.wsclient.project.Project;
import au.com.wp.corp.p6.wsclient.project.ProjectPortType;
import au.com.wp.corp.p6.wsclient.project.ProjectService;

/**
 * @author n039126
 *
 */
public class ProjectServiceCall extends AbstractSOAPCall<List<Project>> {
	private static final Logger log = LoggerFactory.getLogger(ProjectServiceCall.class);
	
	protected ProjectPortType servicePort;
	private final String endPoint; 
	
	public ProjectServiceCall(final RequestTrackingId trackingId) {
		super(trackingId);
		this.endPoint = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_PROJECT_SERVICE_WSDL);
	}

	@Override
	protected void doBefore() throws P6ServiceException {
		if ( null == endPoint ){
			throw new P6ServiceException("Project Service end point is null ");
		}
		URL wsdlURL = null;
		try {
			wsdlURL = new URL(endPoint);
		} catch (MalformedURLException e) {
			throw new P6ServiceException(e);
		}
		
		ProjectService service = new ProjectService(
				wsdlURL);
		servicePort = service.getProjectPort();
		BindingProvider bp = (BindingProvider) servicePort;
		
		Map<String, List<String>> headers = (Map<String, List<String>>) bp.getRequestContext()
				.get("javax.xml.ws.http.request.headers");
		if (headers == null) {
			headers = new HashMap<String, List<String>>();
			bp.getRequestContext().put("javax.xml.ws.http.request.headers", headers);

		}
		log.debug("WS_COOKIE == "+ CacheManager.getWsHeaders().get("WS_COOKIE"));
		
		headers.put("cookie", CacheManager.getWsHeaders().get("WS_COOKIE"));
		
		final List<Handler> handlerChain = bp.getBinding().getHandlerChain();
        handlerChain.add(new SOAPLoggingHandler(trackingId));
        bp.getBinding().setHandlerChain(handlerChain);
	}

	@Override
	protected Holder<List<Project>> command() throws P6ServiceException {
		List<String> fields = new ArrayList<>();
		fields.add("ObjectId");
		fields.add("Name");
		
		List<Project> projects;
		try {
			projects = servicePort.readProjects(fields, null, null);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		}
		
		return new Holder<>(projects);
		
	}
	
	@Override
	protected void doAfter() {

	}

}
