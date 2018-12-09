package me.puneetghodasara.revolut.service;

import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.exception.AccountOperationException;

import java.util.Currency;
import java.util.Optional;

/**
 * A service layer API to work with Account operations </br>
 */
public interface AccountService {

    /**
     * Creates an empty account in specified currency
     * @return
     * @throws AccountOperationException if account opening is unsuccessful
     * @param currency
     */
    AccountEntity open(final Currency currency) throws AccountOperationException;

    /**
     * Credit the passed amount to the account
     *
     * @param accountId account in which amount must be credited
     * @param creditAmount amount to be credited
     * @return true if amount has been credited successfully
     * @throws AccountOperationException
     */
    boolean credit(final String accountId, final Double creditAmount) throws AccountOperationException;

    /**
     * Debits the passed amount from the account
     *
     * @param accountId account from which amount must be debited
     * @param debitAmount amount to be debited
     * @return true if amount has been debited successfully
     * @throws AccountOperationException
     */
    boolean debit(final String accountId, final Double debitAmount) throws AccountOperationException;

    /**
     * Check and returns the amount this account holds.
     * @param accountId account for which balance enquiry is happening
     * @return the amount account has
     */
    Double getBalance(final String accountId) throws AccountOperationException;

    /**
     * Deletes the account permanently
     * @param accountId
     * @return
     * @throws AccountOperationException if account can not be deleted
     */
    boolean deleteAccount(String accountId) throws AccountOperationException;

    /**
     * returns the account info for an account
     * @param accountId
     * @return
     */
    Optional<AccountEntity> getAccount(String accountId);
}
