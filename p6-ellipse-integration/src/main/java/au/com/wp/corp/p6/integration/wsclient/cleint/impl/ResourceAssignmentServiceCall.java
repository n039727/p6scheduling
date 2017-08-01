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
import au.com.wp.corp.p6.wsclient.resourceassignment.ResourceAssignment;
import au.com.wp.corp.p6.wsclient.resourceassignment.ResourceAssignmentPortType;
import au.com.wp.corp.p6.wsclient.resourceassignment.ResourceAssignmentService;

/**
 * @author n039126
 *
 */
public abstract class ResourceAssignmentServiceCall<T> extends AbstractSOAPCall<T> {
	private static final Logger logger1 = LoggerFactory.getLogger(ResourceAssignmentServiceCall.class);

	protected ResourceAssignmentPortType servicePort;
	protected BindingProvider bp;
	protected SOAPLoggingHandler soapHandler;
	
	public ResourceAssignmentServiceCall() throws P6ServiceException{
		super();
		String endPoint = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_RESOURCE_ASSIGNMENT_SERVICE_WSDL);
		if (null == endPoint) {
			throw new P6ServiceException("Resource Assignment Service end point is null ");
		}
		URL wsdlURL = null;
		try {
			wsdlURL = new URL(endPoint);
		} catch (MalformedURLException e) {
			throw new P6ServiceException(e);
		}

		ResourceAssignmentService service = new ResourceAssignmentService(wsdlURL);
		servicePort = service.getResourceAssignmentPort();
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
			headers = new HashMap<>();
			bp.getRequestContext().put("javax.xml.ws.http.request.headers", headers);

		}
		logger1.debug("WS_COOKIE == {}", CacheManager.getWsHeaders().get("WS_COOKIE"));

		headers.put("cookie", CacheManager.getWsHeaders().get("WS_COOKIE"));

	}	
	protected List<ResourceAssignment> readResourceAssigment(final String filter) throws P6ServiceException{
		return null;
	}

	@Override
	protected void doAfter() {

	}

}
