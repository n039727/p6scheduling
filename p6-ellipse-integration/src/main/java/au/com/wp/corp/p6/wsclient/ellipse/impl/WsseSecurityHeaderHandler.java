package au.com.wp.corp.p6.wsclient.ellipse.impl;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.Set;

/**
 * A SOAPHandler that adds an Ellipse WSSE security header to outgoing messages.
 * Usually configured via {@code WsseSecurityConfigurator}.
 *
 * @author n039126
 */
public class WsseSecurityHeaderHandler implements SOAPHandler<SOAPMessageContext> {

    private static final String WSSE_NAMESPACE_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    private static final String WSSE_URI = "wsse";
    private static final String WSU_NAMESPACE_URI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    private static final String WSU_PREFIX = "wsu";
    private static final String WSU_ID_VALUE = "UsernameToken-1478054172";
    public static final String PASSWORD_TYPE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText";
    
    private static final Logger logger = LoggerFactory.getLogger(WsseSecurityHeaderHandler.class);

    private final String userName;
    private final String password;

    public WsseSecurityHeaderHandler(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public boolean handleFault(SOAPMessageContext smc) {
        return true;
    }

    @Override
    public void close(MessageContext mc) {
    }

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
        boolean direction = ((Boolean) smc.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY));

        if (direction) {
            try {
                SOAPEnvelope envelope = smc.getMessage().getSOAPPart().getEnvelope();
                SOAPFactory soapFactory = SOAPFactory.newInstance();

                // WSSecurity <Security> header
                SOAPElement wsSecHeaderElement = soapFactory.createElement("Security", WSSE_URI, WSSE_NAMESPACE_URI);

                SOAPElement userNameTokenElement = soapFactory.createElement("UsernameToken", WSSE_URI, WSSE_NAMESPACE_URI);
                SOAPElement userNameElement = soapFactory.createElement("Username", WSSE_URI, WSSE_NAMESPACE_URI);
                userNameElement.addTextNode(userName);
                SOAPElement passwordElement = soapFactory.createElement("Password", WSSE_URI, WSSE_NAMESPACE_URI);
                passwordElement.addTextNode(password);
                passwordElement.setAttribute("Type", PASSWORD_TYPE);

                userNameTokenElement.addChildElement(userNameElement);
                userNameTokenElement.addChildElement(passwordElement);
                userNameTokenElement.setAttributeNS(WSU_NAMESPACE_URI, WSU_PREFIX + ":Id", WSU_ID_VALUE);

                // add child elements to the root element
                wsSecHeaderElement.addChildElement(userNameTokenElement);

                // create SOAPHeader instance for SOAP envelope
                SOAPHeader soapHeader = envelope.addHeader();

                // add SOAP element for header to SOAP header object
                soapHeader.addChildElement(wsSecHeaderElement);
                
                //Logging soap message request
                log(smc.getMessage());
            } catch (SOAPException ex) {
                throw new RuntimeException("Error create WSSE security header", ex);
            }
        }
        return true;
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    
    private void log (final SOAPMessage message){
    	final ByteArrayOutputStream loggingStream = new ByteArrayOutputStream();
    	try {
			message.writeTo(loggingStream);
			loggingStream.write("".getBytes());
		} catch (final Exception e) {
		}
    	logger.debug("Inbound Message #  {}", loggingStream.toString());
    }
}