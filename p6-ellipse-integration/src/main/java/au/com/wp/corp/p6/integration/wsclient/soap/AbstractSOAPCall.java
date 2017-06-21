package au.com.wp.corp.p6.integration.wsclient.soap;

import java.rmi.RemoteException;

import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.wsclient.logging.RequestTrackingId;

/**
 * Calls the web service 
 * @author n039126
 * @version 1.0
 * @param <T>
 */
public abstract class AbstractSOAPCall<T> {

    protected RequestTrackingId trackingId;
    static Logger logger = LoggerFactory.getLogger(AbstractSOAPCall.class);

    public AbstractSOAPCall(final RequestTrackingId trackingId) {
        this.trackingId = trackingId;
    }

    public Holder<T> run() throws P6ServiceException {
        Holder<T> holder = null;
        try {
        	 doBefore();
            holder = command();
        } catch (final P6ServiceException e) {
            logger.error("Tracking Id: {} # Catched Exception : {} ", trackingId, e);
            logger.error("Tracking Id: {} # Error Code : {} ", trackingId, e.getMessage());
            logger.error("Tracking Id: {} # Exception during SOAP call : {} ", trackingId, e.getCause());
            logger.error("Tarcking Id: {} # Stacktrace of the Exception : ", trackingId, e.getCause());
            throw new P6ServiceException ("DATA_ERROR", e.getCause());
        } catch ( WebServiceException e){
        	throw new P6ServiceException("TECHNICAL_ERROR",e);
        }  catch ( Exception e){
        	throw new P6ServiceException("TECHNICAL_ERROR",e);
        } 

        doAfter();
        return holder;
    }

    protected abstract void doBefore() throws P6ServiceException ;

    protected abstract Holder<T> command() throws P6ServiceException;

    protected abstract void doAfter();

}
