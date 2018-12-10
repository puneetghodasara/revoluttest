package me.puneetghodasara.revolut.model;

import me.puneetghodasara.revolut.entity.Transaction;
import me.puneetghodasara.revolut.entity.TransactionStatus;
import me.puneetghodasara.revolut.entity.TransactionStatusMessage;

import java.io.Serializable;

/**
 * This class models the Transaction Response and can be transferred </br>
 * from backend to frontend with JSON serializer.
 */
public class TransactionReponse implements Serializable {

    private final String transactionId;

    private final TransactionStatus transactionStatus;

    private final TransactionStatusMessage transactionStatusMessage;

    public TransactionReponse(final String transactionId,
                              final TransactionStatus transactionStatus,
                              final TransactionStatusMessage transactionStatusMessage) {
        this.transactionId = transactionId;
        this.transactionStatus = transactionStatus;
        this.transactionStatusMessage = transactionStatusMessage;
    }

    public static TransactionReponse fromEntity(final Transaction transaction){
        return new TransactionReponse(transaction.getTransactionId(),
                transaction.getTransactionStatus(),
                transaction.getTxStatusMessage());
    }
}
