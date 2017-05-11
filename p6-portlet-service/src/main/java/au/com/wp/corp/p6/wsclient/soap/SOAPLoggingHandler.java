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

import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;


public class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {

    private static Logger logger = LoggerFactory.getLogger(SOAPLoggingHandler.class);

    private final RequestTrackingId trackingId;
    private long requestTimeStamp;
    private long responseTimeStamp;

    public SOAPLoggingHandler(final RequestTrackingId trackingId) {
        this.trackingId = trackingId;
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
        final Boolean outboundProperty = (Boolean) soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        final ByteArrayOutputStream loggingStream = new ByteArrayOutputStream();

        try {
            if (outboundProperty.booleanValue()) {
                requestTimeStamp = System.currentTimeMillis();
                logger.debug("Tracking Id: {} # SOAP Service request timestamp:  {} ms", trackingId, requestTimeStamp);
                loggingStream.write(("\n" + trackingId + " Outbound message: ").getBytes());
            } else {
                responseTimeStamp = System.currentTimeMillis();
                logger.debug("Tracking Id: {} # SOAPService response timestamp: {} ms", trackingId, responseTimeStamp);
                loggingStream.write(("\n" + trackingId + " Inbound message: ").getBytes());
            }

            final SOAPMessage message = soapMessageContext.getMessage();
            try {
                message.writeTo(loggingStream);
                loggingStream.write("".getBytes());
            } catch (final Exception e) {
                loggingStream.write(("\n" + trackingId + " Exception in handler: " + e).getBytes());
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        logger.debug(loggingStream.toString());
        if (responseTimeStamp > requestTimeStamp) {
            final long serviceCallTime = responseTimeStamp - requestTimeStamp;
            logger.debug("TrackingId : {} # SOAP Service response time taken by BackEnd system : {} ms", trackingId, serviceCallTime);
        }
    }
}
