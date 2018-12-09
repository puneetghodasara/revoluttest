package me.puneetghodasara.revolut.service;

import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.entity.UserEntity;
import me.puneetghodasara.revolut.exception.AccountOperationException;
import me.puneetghodasara.revolut.exception.UserOperationException;

import java.util.Currency;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A service layer API to work with User operations </br>
 */
public interface UserService {

    /**
     * Gets all {@link UserEntity} users
     * @return
     */
    Stream<UserEntity> getUsers();

    /**
     * @param userId
     * @return UserEntity if found containing userId
     */
    Optional<UserEntity> getUser(final String userId);

    /**
     * @param userId
     * @return newly registered UserEntity
     * @throws UserOperationException if user can not be registered
     */
    void register(final String userId) throws UserOperationException;

    /**
     * @param userId
     * @return true if user has unregistered
     * @throws UserOperationException if user can not be unregistered
     */
    boolean unregister(String userId) throws UserOperationException, AccountOperationException;

    /**
     * Adds an account to registered user </br>
     * It will override if it is already added.
     *
     * @param userId
     * @param currency
     * @return newly added account
     * @throws UserOperationException if user id is wrong
     * @throws AccountOperationException if account could not be opened
     */
    AccountEntity addAccount(String userId, Currency currency) throws UserOperationException, AccountOperationException;

    /**
     * Removes account from user
     * @param userId
     * @param accountId
     * @throws UserOperationException if user id is wrong
     * @throws AccountOperationException if account could not be deleted.
     */
    void removeAccount(String userId, String accountId) throws UserOperationException, AccountOperationException;

    /**
     *
     * @param userId
     * @param accountId
     * @return AccountEntity
     */
    AccountEntity getUserAccount(String userId, String accountId) throws UserOperationException, AccountOperationException;
}
