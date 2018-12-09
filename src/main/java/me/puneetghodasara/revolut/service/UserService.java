package me.puneetghodasara.revolut.service;

import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.entity.UserEntity;
import me.puneetghodasara.revolut.exception.UserOperationException;

/**
 * A service layer API to work with User operations </br>
 */
public interface UserService {

    /**
     * Registers a new UserID
     * @param userId
     * @return newly registered UserEntity
     * @throws UserOperationException
     */
    void register(final String userId) throws UserOperationException;

    /**
     * Adds an account to registered user </br>
     * It will override if it is already added.
     * @param accountEntity
     * @return
     * @throws UserOperationException
     */
    UserEntity addAccount(UserEntity userEntity, AccountEntity accountEntity) throws UserOperationException;
}
