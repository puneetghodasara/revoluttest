package me.puneetghodasara.revolut.exception;

public class AccountOperationException extends Exception {

    public enum AccountOperationExceptionMessages {
        /**
         * If Amount is not positive number
         */
        INVALID_AMOUNT,
        /**
         * If Balance is not sufficient to be debited
         */
        INSUFFICIENT_BALANCE,
        /**
         * If Account opening failed
         */
        ERROR_OPENING_ACCOUNT,
        /**
         * If Account deletion failed
         */
        ERROR_DELETEING_ACCOUNTS,
        /**
         * Unknown Account ID
         */
        UNKNOWN_ACCOUNT,
        /**
         * If account balance is not zero (and trying to delete)
         */
        NONEMPTY_ACCOUNTS,
        /**
         * Account ID is not registered to User ID
         */
        UNKNOWN_ACCOUNT_FOR_USER,
        /**
         * Failed to debit from the account
         */
        DEBIT_FAILED,
        /**
         * Failed to credit to the account
         */
        CREDIT_FAILED;
    }

    public AccountOperationException(final AccountOperationExceptionMessages message) {
        super(message.name());
    }
}
