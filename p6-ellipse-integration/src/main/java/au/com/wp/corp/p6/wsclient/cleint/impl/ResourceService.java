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
import au.com.wp.corp.p6.util.CacheManager;
import au.com.wp.corp.p6.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.wsclient.constant.P6EllipseWSConstants;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.resource.Resource;
import au.com.wp.corp.p6.wsclient.resource.ResourceFieldType;
import au.com.wp.corp.p6.wsclient.resource.ResourcePortType;
import au.com.wp.corp.p6.wsclient.soap.AbstractSOAPCall;
import au.com.wp.corp.p6.wsclient.soap.SOAPLoggingHandler;

public class ResourceService extends AbstractSOAPCall<List<Resource>> {
	private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);

	private BindingProvider bp;
	private ResourcePortType servicePort;
	private String endPoint;
	private String filter;
	private final String orderBy;

	public ResourceService(final RequestTrackingId trackingId, String filter, String orderBy) {
		super(trackingId);
		this.filter = filter;
		this.endPoint = P6ReloadablePropertiesReader.getProperty(P6EllipseWSConstants.P6_RESOURCE_SERVICE_WSDL);
		this.orderBy = orderBy;
	}

	@Override
	protected void doBefore() throws P6ServiceException {
		if ( null == endPoint ){
			throw new P6ServiceException("Resource Service end point is null ");
		}
		URL wsdlURL = null;
		try {
			wsdlURL = new URL(endPoint);
		} catch (MalformedURLException e) {
			throw new P6ServiceException(e);
		}
		au.com.wp.corp.p6.wsclient.resource.ResourceService Service = new au.com.wp.corp.p6.wsclient.resource.ResourceService(
				wsdlURL, new QName("http://xmlns.oracle.com/Primavera/P6/WS/Resource/V1", "ResourceService"));
		servicePort = Service.getResourcePort();
		bp = (BindingProvider) servicePort;

		Map<String, List<String>> headers = (Map<String, List<String>>) bp.getRequestContext()
				.get("javax.xml.ws.http.request.headers");
		if (headers == null) {
			headers = new HashMap<String, List<String>>();
			bp.getRequestContext().put("javax.xml.ws.http.request.headers", headers);

		}
		logger.debug("WS_COOKIE == " + CacheManager.getWsHeaders().get("WS_COOKIE"));

		headers.put("cookie", CacheManager.getWsHeaders().get("WS_COOKIE"));

		final List<Handler> handlerChain = bp.getBinding().getHandlerChain();
		handlerChain.add(new SOAPLoggingHandler(trackingId));
		bp.getBinding().setHandlerChain(handlerChain);

	}

	@Override
	protected Holder<List<Resource>> command() throws P6ServiceException {

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
		}

		return new Holder<>(resources);
	}

	@Override
	protected void doAfter() {
		// TODO Auto-generated method stub

	}

}
