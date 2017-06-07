package au.com.wp.corp.p6.wsclient.ellipse.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mincom.ews.service.connectivity.OperationContext;
import com.mincom.ews.service.transaction.Transaction;

import au.com.wp.corp.p6.wsclient.ellipse.EllipseTransactionService;

/**
 * Service used for creating and completing transactions in Ellipse.
 */
@Service
public class EllipseTransactionServiceImpl implements EllipseTransactionService {
	
	private static final Logger LOG = Logger.getLogger(EllipseTransactionServiceImpl.class);
	
	private Transaction txService;

	/**
	 * Starts an Ellipse transaction. The returned identifier must be passed in to all subsequent Ellipse service
	 * invocations to ensure that they are processed in this transaction.
	 * 
	 * @return The transaction ID.
	 */
	@Override
	public String beginTransaction() {
		LOG.debug("Beginning transaction.");
		OperationContext ctx = EllipseWebServiceHelper.generateContext(null);
		String txId = txService.begin(ctx);
		LOG.trace("Transaction ID: " + txId);
		return txId;
	}
	
	/**
	 * Commits an Ellipse transaction.
	 * 
	 * @param txId The transaction ID.
	 */
	@Override
	public void commitTransaction(String txId) {
		LOG.debug(String.format("Committing transaction \"%s\".", txId));
		OperationContext ctx = EllipseWebServiceHelper.generateContext(txId);
		txService.commit(ctx);
	}
	
	/**
	 * Rolls back an Ellipse transaction.
	 * 
	 * @param txId The transaction ID.
	 */
	@Override
	public void rollbackTransaction(String txId) {
		LOG.warn(String.format("Rolling back transaction \"%s\".", txId)); 
		OperationContext ctx = EllipseWebServiceHelper.generateContext(txId);
		txService.rollback(ctx);
	}
	
	@Autowired
	public void setTransactionService(Transaction txService) {
		this.txService = txService;
	}
}
