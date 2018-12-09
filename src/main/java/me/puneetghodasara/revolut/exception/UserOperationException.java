package me.puneetghodasara.revolut.exception;

public class UserOperationException extends Exception {
    public enum UserOperationExceptionMessages {
        ERROR_REGISTERING_USER,
        INVALID_USERID,
        NONEMPTY_ACCOUNTS,
        ERROR_DELETEING_ACCOUNTS,
        USERID_TAKEN
    }

    public UserOperationException(final UserOperationException.UserOperationExceptionMessages message) {
        super(message.name());
    }
}
