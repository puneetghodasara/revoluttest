package me.puneetghodasara.revolut.exception;

public class TransactionException extends Exception {


    public enum TransactionExceptionMessages {FAIL_TO_ACCEPT, UNKNOWN_TRANSACTION_ID;}

    public TransactionException(final TransactionExceptionMessages failToAccept) {
        super(failToAccept.name());
    }
}
