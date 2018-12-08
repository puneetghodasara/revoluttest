package me.puneetghodasara.revolut.exception;

public class UserOperationException extends Exception {
    public enum UserOperationExceptionMessages {
        USERID_TAKEN
    }

    public UserOperationException(final UserOperationException.UserOperationExceptionMessages message) {
        super(message.name());
    }
}
