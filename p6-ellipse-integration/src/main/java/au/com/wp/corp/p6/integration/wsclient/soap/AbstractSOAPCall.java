package au.com.wp.corp.p6.integration.wsclient.soap;

import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.integration.exception.P6ExceptionType;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.wsclient.logging.RequestTrackingId;

/**
 * Calls the web service 
 * @author n039126
 * @version 1.0
 * @param <T>
 */
public abstract class AbstractSOAPCall<T> {
	
	private static final String INVALID_USER_NAME = "Invalid user name and/or password";

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
            logException(e);
            if ( e.getMessage().contains(INVALID_USER_NAME)){
            	throw new P6ServiceException (P6ExceptionType.SYSTEM_ERROR.name(), e.getCause());
            }
            throw new P6ServiceException (P6ExceptionType.DATA_ERROR.name(), e.getCause());
        } catch ( WebServiceException e){
        	logException(e);
        	throw new P6ServiceException(P6ExceptionType.SYSTEM_ERROR.name(),e);
        }  catch ( Exception e){
        	logException(e);
        	throw new P6ServiceException(P6ExceptionType.SYSTEM_ERROR.name(),e);
        } 

        doAfter();
        return holder;
    }

	/**
	 * @param e
	 */
	private void logException(final Exception e) {
		logger.error("Tracking Id: {} # Catched Exception : {} ", trackingId, e);
		logger.error("Tracking Id: {} # Error Code : {} ", trackingId, e.getMessage());
		logger.error("Tracking Id: {} # Exception during SOAP call : {} ", trackingId, e.getCause());
		logger.error("Tarcking Id: {} # Stacktrace of the Exception : ", trackingId, e.getCause());
	}

    protected abstract void doBefore() throws P6ServiceException ;

    protected abstract Holder<T> command() throws P6ServiceException;

    protected abstract void doAfter();

}
