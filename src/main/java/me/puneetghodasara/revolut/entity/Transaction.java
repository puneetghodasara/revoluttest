package me.puneetghodasara.revolut.entity;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A class that represents transaction </br>
 * Non-Immutable simplified model of transaction
 */
public class Transaction {

    private final String sourceAccount;
    private final String targetAccount;
    private final Double amount;
    private final String message;
    private final String transactionId;
    private TransactionStatus transactionStatus;
    private TransactionStatusMessage txStatusMessage;

    private static final AtomicLong transactionSequence = new AtomicLong();

    // Requires private access as we go for builder pattern
    private Transaction(final String sourceAccount,
                        final String targetAccount,
                        final Double amount, final String message) {
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.amount = amount;
        this.message = message;
        transactionStatus = TransactionStatus.NEW;
        transactionId = String.valueOf(transactionSequence.incrementAndGet());
    }

    public String getSourceAccount() {
        return sourceAccount;
    }

    public String getTargetAccount() {
        return targetAccount;
    }

    public Double getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public TransactionStatusMessage getTxStatusMessage() {
        return txStatusMessage;
    }

    public void updateTransactionStatus(final TransactionStatus newStatus, final TransactionStatusMessage txStatusMessage){
        this.transactionStatus = newStatus;
        this.txStatusMessage = txStatusMessage;
    }

    public static class Builder {

        private String sourceAccount;
        private String targetAccount;
        private Double amount;
        private String message = "";

        public Builder(final String sourceAccount, final String targetAccount, final Double amount) {
            this.sourceAccount = sourceAccount;
            this.targetAccount = targetAccount;
            this.amount = amount;
        }

        public Builder message(final String message) {
            this.message = message;
            return this;
        }

        public Transaction build() {
            return new Transaction(this.sourceAccount, this.targetAccount, this.amount, this.message);
        }
    }

}
