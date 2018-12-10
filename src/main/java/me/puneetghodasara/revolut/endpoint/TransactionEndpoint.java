package me.puneetghodasara.revolut.endpoint;

import me.puneetghodasara.revolut.exception.TransactionException;
import me.puneetghodasara.revolut.model.TransactionReponse;

/**
 * The Main interface for money transaction </br>
 */
public interface TransactionEndpoint {

    /**
     * Creates a transaction and add it to transaction Queue to be picked up by transaction manager </br>
     * @param sourceAccount accountId from which amount would be debited
     * @param targetAccount accountId to which amount would be credited
     * @param amount amount to be transfer
     * @param message
     * @return transactionId to track the status
     */
    String transact(final String sourceAccount, final String targetAccount, final Double amount, final String message) throws TransactionException;

    /**
     * Retrieves transaction status for a given transactionID
     * @param transactionId
     * @return
     */
    TransactionReponse getStatus(final String transactionId) throws TransactionException;
}
