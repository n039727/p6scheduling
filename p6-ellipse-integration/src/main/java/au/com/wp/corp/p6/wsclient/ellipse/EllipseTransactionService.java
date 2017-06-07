package au.com.wp.corp.p6.wsclient.ellipse;

/**
 * Provides an interface to the transaction service in Ellipse. The transactions created here are used by other Ellipse
 * services to perform their tasks in an atomic manner.
 */
public interface EllipseTransactionService {
	/**
	 * Starts an Ellipse transaction. The returned identifier must be passed in to all subsequent Ellipse service
	 * invocations to ensure that they are processed in this transaction.
	 * 
	 * @return The transaction ID.
	 */
	public String beginTransaction();

	/**
	 * Commits an Ellipse transaction.
	 * 
	 * @param txId The transaction ID.
	 */
	public void commitTransaction(String txId);

	/**
	 * Rolls back an Ellipse transaction.
	 * 
	 * @param txId The transaction ID.
	 */
	public void rollbackTransaction(String txId);
}