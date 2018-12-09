package me.puneetghodasara.revolut.service;

import me.puneetghodasara.revolut.dao.AccountRepository;
import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.exception.AccountOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Currency;


public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;
    private final AccountNumberService accountNumberService;

    public AccountServiceImpl(final AccountRepository accountRepository, final AccountNumberService accountNumberService) {
        this.accountRepository = accountRepository;
        this.accountNumberService = accountNumberService;
    }

    @Override
    public AccountEntity open(final Currency currency) throws AccountOperationException {
        try {
            final String accountId = accountNumberService.nextAccountNumber();
            final AccountEntity newAccount = new AccountEntity(accountId, currency, 0d);
            accountRepository.updateAccount(newAccount);
            return accountRepository.getAccount(accountId)
                    .orElseThrow(() -> new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.ERROR_OPENING_ACCOUNT));
        } catch (Exception e) {
            throw new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.ERROR_OPENING_ACCOUNT);
        }
    }

    @Override
    public boolean credit(final AccountEntity accountEntity, final Double creditAmount) throws AccountOperationException {
        logger.debug("Trying to credit amount {} to account {}", creditAmount, accountEntity.getAccountId());

        if (creditAmount <= 0) {
            throw new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.INVALID_CREDIT_ACCOUNT);
        }

        accountEntity.getAmountLock().writeLock().lock();
        try {
            final Double currentBalance = getBalance(accountEntity);
            final double newAmountValue = currentBalance + creditAmount;
            final AccountEntity newAccount = accountEntity.withNewAmount(newAmountValue);
            accountRepository.updateAccount(newAccount);
        } catch (final Exception e) {
            // Take all exception into consideration
            logger.warn("credit failed for account {}", accountEntity.getAccountId());
            return false;
        } finally {
            accountEntity.getAmountLock().writeLock().unlock();
        }
        return true;
    }

    @Override
    public boolean debit(final AccountEntity accountEntity, final Double debitAmount) throws AccountOperationException {
        logger.debug("Trying to debit amount {} to account {}", debitAmount, accountEntity.getAccountId());

        if (debitAmount <= 0) {
            throw new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.INVALID_DEBIT_ACCOUNT);
        }

        accountEntity.getAmountLock().writeLock().lock();
        try {
            final Double currentBalance = getBalance(accountEntity);
            if (currentBalance < debitAmount) {
                throw new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.INSUFFICIENT_BALANCE);
            }
            final double newAmountValue = currentBalance - debitAmount;
            final AccountEntity newAccount = accountEntity.withNewAmount(newAmountValue);
            accountRepository.updateAccount(newAccount);
        } catch (final Exception e) {
            // Take all exception into consideration
            logger.warn("credit failed for account {}", accountEntity.getAccountId());
            return false;
        } finally {
            accountEntity.getAmountLock().writeLock().unlock();
        }
        return true;
    }

    @Override
    public Double getBalance(final AccountEntity accountEntity) throws AccountOperationException {
        accountEntity.getAmountLock().readLock().lock();
        try {
            return accountRepository.getAccount(accountEntity.getAccountId())
                    .orElseThrow(() -> new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.UNKNOWN_ACCOUNT))
                    .getAmount();
        } finally {
            accountEntity.getAmountLock().readLock().unlock();
        }
    }
}
