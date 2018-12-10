package me.puneetghodasara.revolut.model;

import me.puneetghodasara.revolut.entity.Transaction;
import me.puneetghodasara.revolut.entity.TransactionStatus;

import java.io.Serializable;

/**
 * This class models the Transaction Response and can be transferred </br>
 * from backend to frontend with JSON serializer.
 */
public class TransactionReponse implements Serializable {

    private final String transactionId;

    private final TransactionStatus transactionStatus;

    private final String transactionStatusMessage;

    public TransactionReponse(final String transactionId,
                              final TransactionStatus transactionStatus,
                              final String transactionStatusMessage) {
        this.transactionId = transactionId;
        this.transactionStatus = transactionStatus;
        this.transactionStatusMessage = transactionStatusMessage;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public String getTransactionStatusMessage() {
        return transactionStatusMessage;
    }

    public static TransactionReponse fromEntity(final Transaction transaction){
        return new TransactionReponse(transaction.getTransactionId(),
                transaction.getTransactionStatus(),
                transaction.getTxStatusMessage());
    }
}
