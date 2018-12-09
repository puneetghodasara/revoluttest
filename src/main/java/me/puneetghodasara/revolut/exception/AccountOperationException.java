package me.puneetghodasara.revolut.exception;

public class AccountOperationException extends Exception {

    public enum AccountOperationExceptionMessages {
        INVALID_CREDIT_ACCOUNT,
        INVALID_DEBIT_ACCOUNT,
        INSUFFICIENT_BALANCE,
        ERROR_OPENING_ACCOUNT,
        UNKNOWN_ACCOUNT;
    }

    public AccountOperationException(final AccountOperationExceptionMessages message) {
        super(message.name());
    }
}
