package me.puneetghodasara.revolut.service;

import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.exception.AccountOperationException;

import java.util.Currency;

/**
 * A service layer API to work with Account operations </br>
 */
public interface AccountService {

    /**
     * Creates an empty account
     * @return
     * @throws AccountOperationException
     * @param currency
     */
    AccountEntity open(final Currency currency) throws AccountOperationException;

    /**
     * Credit the passed amount to the account
     *
     * @param accountEntity account in which amount must be credited
     * @param creditAmount amount to be credited
     * @return true if amount has been credited successfully
     * @throws AccountOperationException
     */
    boolean credit(final AccountEntity accountEntity, final Double creditAmount) throws AccountOperationException;

    /**
     * Debits the passed amount from the account
     *
     * @param accountEntity account from which amount must be debited
     * @param debitAmount amount to be debited
     * @return true if amount has been debited successfully
     * @throws AccountOperationException
     */
    boolean debit(final AccountEntity accountEntity, final Double debitAmount) throws AccountOperationException;

    /**
     * Check and returns the amount this account holds.
     * @param accountEntity account for which balance enquiry is happening
     * @return the amount account has
     */
    Double getBalance(final AccountEntity accountEntity) throws AccountOperationException;
}
