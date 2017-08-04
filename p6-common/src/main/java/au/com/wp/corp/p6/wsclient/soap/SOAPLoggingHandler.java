package au.com.wp.corp.p6.wsclient.soap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.utils.P6ReloadablePropertiesReader;

/**
 * SOAPLOggingHandler logs the web service request and response and also capture
 * the service response time
 * 
 * @author N039126
 * @version 1.0
 */

public class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {

	private static Logger logger = LoggerFactory.getLogger(SOAPLoggingHandler.class);

	private long requestTimeStamp;
	private long responseTimeStamp;
	
	private static final String P6_PRINT_REQ_RES_XML_ON = "P6_PRINT_REQ_RES_XML_ON";

	private String P6_PRINT_REQ_RES_XML_FLAG;
	
	public SOAPLoggingHandler() {
				
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	@Override
	public boolean handleMessage(final SOAPMessageContext soapMessageContext) {
		log(soapMessageContext);
		return true;
	}

	@Override
	public boolean handleFault(final SOAPMessageContext soapMessageContext) {
		log(soapMessageContext);
		return true;
	}

	@Override
	public void close(final MessageContext messageContext) {
	}

	private void log(final SOAPMessageContext soapMessageContext) {
		this.P6_PRINT_REQ_RES_XML_FLAG = P6ReloadablePropertiesReader.getProperty(P6_PRINT_REQ_RES_XML_ON);
		final Boolean outboundProperty = (Boolean) soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		final ByteArrayOutputStream loggingStream = new ByteArrayOutputStream();

		try {
			if (outboundProperty.booleanValue()) {
				requestTimeStamp = System.currentTimeMillis();
				logger.debug("SOAP Service request timestamp:  {} ms", requestTimeStamp);
				loggingStream.write(("\n" + " Outbound message: ").getBytes());
			} else {
				responseTimeStamp = System.currentTimeMillis();
				logger.debug("SOAPService response timestamp: {} ms", responseTimeStamp);
				loggingStream.write(("\n" + " Inbound message: ").getBytes());
			}

			final SOAPMessage message = soapMessageContext.getMessage();
			try {
				message.writeTo(loggingStream);
				loggingStream.write("".getBytes());
			} catch (final Exception e) {
				loggingStream.write(("\n" + " Exception in handler: " + e).getBytes());
			}
		} catch (final IOException e) {
			logger.error("An error occurs while logging SOAP service call details", e);
		}
		
		
		if ( this.P6_PRINT_REQ_RES_XML_FLAG != null && this.P6_PRINT_REQ_RES_XML_FLAG.equals("Y"))
			logger.info(loggingStream.toString());
		if (responseTimeStamp > requestTimeStamp) {
			final long serviceCallTime = responseTimeStamp - requestTimeStamp;
			logger.info("SOAP Service response time taken by BackEnd system : {} ms", serviceCallTime);
		}
	}
}
