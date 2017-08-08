package au.com.wp.corp.p6.wsclient.soap;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.utils.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;

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
    	boolean successful = false;
    	int p6Maxretry = Integer.parseInt(P6ReloadablePropertiesReader.getProperty("P6_MAX_RETRY"));
		int retryCount = 0;
		Holder<T> holder= null;
		do{
			try {
				doBefore();
				holder = command();
				successful = true;
			} catch (final Exception e) {
	
	            logger.error("Tracking Id: {} # Catched Exception : {} ", trackingId, e);
	            logger.error("Tracking Id: {} # Error Code : {} ", trackingId, e.getMessage());
	            logger.error("Tracking Id: {} # Exception during SOAP call : {} ", trackingId, e.getCause());
	            logger.error("Tarcking Id: {} # Stacktrace of the Exception : ", trackingId, e.getCause());
	            retryCount++;
				logger.info("Retry count for P6 service {}", retryCount );
				if(retryCount<p6Maxretry){
					try {
						logger.debug("Going to wait for a second...");
					    Thread.sleep(Long.parseLong(P6ReloadablePropertiesReader.getProperty("P6_RETRY_WAIT_TIME")));                 
					} catch(InterruptedException ex) {
						logger.info("Thread.sleep got InterruptedException >>{}", ex.getMessage() );
					    Thread.currentThread().interrupt();
					}
					continue;
				}
				else{
					throw e;
				}
	        }
		} while (!successful && retryCount<p6Maxretry);
        doAfter();
        return holder;
    }

    protected abstract void doBefore() throws P6ServiceException ;

    protected abstract Holder<T> command() throws P6ServiceException;

    protected abstract void doAfter();

}
