package me.puneetghodasara.revolut.exception;

public class TransactionException extends Exception {


    public enum TransactionExceptionMessages {
        /**
         * Fail to pass transaction to broker
         */
        FAIL_TO_ACCEPT,
        /**
         * Invalid Transaction ID
         */
        UNKNOWN_TRANSACTION_ID;

    }

    public TransactionException(final TransactionExceptionMessages failToAccept) {
        super(failToAccept.name());
    }
}
