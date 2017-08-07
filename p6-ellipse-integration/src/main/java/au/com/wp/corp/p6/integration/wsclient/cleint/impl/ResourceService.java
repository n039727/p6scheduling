package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
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
import au.com.wp.corp.p6.wsclient.resource.Resource;
import au.com.wp.corp.p6.wsclient.resource.ResourceFieldType;
import au.com.wp.corp.p6.wsclient.resource.ResourcePortType;

@Component
@Lazy
public class ResourceService extends AbstractSOAPCall<List<Resource>> {
	private static final Logger logger1 = LoggerFactory.getLogger(ResourceService.class);

	private ResourcePortType servicePort;
	private String filter;
	private String orderBy;
	protected BindingProvider bp;
	protected SOAPLoggingHandler soapHandler;

	public ResourceService() throws P6ServiceException{
		super();
		String endPoint = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_RESOURCE_SERVICE_WSDL);
		if ( null == endPoint ){
			throw new P6ServiceException("Resource Service end point is null ");
		}
		URL wsdlURL = null;
		try {
			wsdlURL = new URL(endPoint);
		} catch (MalformedURLException e) {
			throw new P6ServiceException(e);
		}
		au.com.wp.corp.p6.wsclient.resource.ResourceService service = new au.com.wp.corp.p6.wsclient.resource.ResourceService(
				wsdlURL, new QName("http://xmlns.oracle.com/Primavera/P6/WS/Resource/V1", "ResourceService"));
		this.servicePort = service.getResourcePort();
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
		logger1.debug("WS_COOKIE == " + CacheManager.getWsHeaders().get("WS_COOKIE"));

		headers.put("cookie", CacheManager.getWsHeaders().get("WS_COOKIE"));

	}

	@Override
	protected List<Resource> command() throws P6ServiceException {

		List<ResourceFieldType> fields = new ArrayList<>();

		fields.add(ResourceFieldType.ID);
		fields.add(ResourceFieldType.NAME);
		fields.add(ResourceFieldType.OBJECT_ID);
		fields.add(ResourceFieldType.PRIMARY_ROLE_NAME);
		fields.add(ResourceFieldType.PRIMARY_ROLE_ID);
		fields.add(ResourceFieldType.RESOURCE_TYPE);
		fields.add(ResourceFieldType.TITLE);
		fields.add(ResourceFieldType.IS_ACTIVE);
		fields.add(ResourceFieldType.EMPLOYEE_ID);

		List<Resource> resources;
		try {
			resources = servicePort.readResources(fields, filter, orderBy);

		} catch (au.com.wp.corp.p6.wsclient.resource.IntegrationFault e) {
			throw new P6ServiceException(e);
		} finally {
			this.filter = null;
			this.orderBy = null;
		}

		return resources;
	}

	
	public List<Resource> readResource (String filter, String orderBy) throws P6ServiceException {
		this.filter = filter;
		this.orderBy = orderBy;
		return run();
	}
	
	@Override
	protected void doAfter() {
	}

}
