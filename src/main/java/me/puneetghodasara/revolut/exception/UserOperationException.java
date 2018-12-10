package me.puneetghodasara.revolut.exception;

public class UserOperationException extends Exception {
    public enum UserOperationExceptionMessages {
        /**
         * User registration failed
         */
        ERROR_REGISTERING_USER,
        /**
         * User ID is not valid
         */
        INVALID_USERID,
        /**
         * One or more account of users could not be deleted.
         */
        ERROR_DELETEING_ACCOUNTS,
        /**
         * Request to register duplicate user id
         */
        USERID_TAKEN
    }

    public UserOperationException(final UserOperationException.UserOperationExceptionMessages message) {
        super(message.name());
    }
}
