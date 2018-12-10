package me.puneetghodasara.revolut.service;

import me.puneetghodasara.revolut.dao.AccountInMemoryDao;
import me.puneetghodasara.revolut.dao.AccountRepository;
import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.entity.Transaction;
import me.puneetghodasara.revolut.entity.TransactionStatus;
import me.puneetghodasara.revolut.exception.AccountOperationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Currency;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class TransactionProcessorTest {

    private TransactionQueueBroker testTxBroker;
    private TransactionProcessor testTxProcessor;
    private TransactionProcessor testErrorTxProcessor;
    private AccountService testAccountService;
    private AccountService testErrorAccountService;

    class ErrorenousAccountServiceImpl extends AccountServiceImpl {

        public ErrorenousAccountServiceImpl(final AccountRepository accountRepository, final AccountNumberService accountNumberService) {
            super(accountRepository, accountNumberService);
        }

        @Override
        public boolean credit(final String accountId, final Double creditAmount) throws AccountOperationException {
            throw new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.CREDIT_FAILED);
        }
    }

    @Before
    public void setUp() throws Exception {
        // All parameters can be mocked
        testTxBroker = new TransactionQueueBroker();
        AccountInMemoryDao accountRepository = new AccountInMemoryDao();
        testAccountService = new AccountServiceImpl(accountRepository, new AccountNumberServiceImpl());
        testTxProcessor = new TransactionProcessor(testTxBroker, testAccountService);
        testErrorAccountService = new ErrorenousAccountServiceImpl(accountRepository, new AccountNumberServiceImpl());
        testErrorTxProcessor = new TransactionProcessor(testTxBroker, testErrorAccountService);
    }

    @Test
    public void testPositive() throws AccountOperationException {
        final String accountId1 = testAccountService.open(Currency.getInstance("EUR")).getAccountId();
        testAccountService.credit(accountId1, 11D);
        final String accountId2 = testAccountService.open(Currency.getInstance("EUR")).getAccountId();
        final Transaction transaction = new Transaction.Builder(accountId1, accountId2, 10D).build();
        testTxBroker.addTransaction(transaction);
        testTxProcessor.run();

        Assert.assertEquals(TransactionStatus.SUCCESS, transaction.getTransactionStatus());

        assertEquals(1, testAccountService.getBalance(accountId1), 0.00001);
        assertEquals(10, testAccountService.getBalance(accountId2), 0.00001);
    }

    @Test
    public void testInsufficientBalance() throws AccountOperationException {
        final String accountId1 = testAccountService.open(Currency.getInstance("EUR")).getAccountId();
        testAccountService.credit(accountId1, 9D);
        final String accountId2 = testAccountService.open(Currency.getInstance("EUR")).getAccountId();
        final Transaction transaction = new Transaction.Builder(accountId1, accountId2, 10D).build();
        testTxBroker.addTransaction(transaction);
        testTxProcessor.run();

        Assert.assertEquals(TransactionStatus.ERROR, transaction.getTransactionStatus());
        Assert.assertEquals(AccountOperationException.AccountOperationExceptionMessages.INSUFFICIENT_BALANCE.name(), transaction.getTxStatusMessage());

        assertEquals(9, testAccountService.getBalance(accountId1), 0.00001);
        assertEquals(0, testAccountService.getBalance(accountId2), 0.00001);
    }

    @Test
    public void testCreditDisallow() throws AccountOperationException {
        final String accountId1 = testAccountService.open(Currency.getInstance("EUR")).getAccountId();
        testAccountService.credit(accountId1, 11D);
        final String accountId2 = testAccountService.open(Currency.getInstance("EUR")).getAccountId();
        final Transaction transaction = new Transaction.Builder(accountId1, accountId2, 10D).build();
        testTxBroker.addTransaction(transaction);

        // Run with error so it will be debited but not credited
        testErrorTxProcessor.run();
        Assert.assertEquals(TransactionStatus.DEBIT_SUCCESS, transaction.getTransactionStatus());

        assertEquals(1, testAccountService.getBalance(accountId1), 0.00001);
        assertEquals(0, testAccountService.getBalance(accountId2), 0.00001);

        // Second run should pass
        testTxProcessor.run();

        Assert.assertEquals(TransactionStatus.SUCCESS, transaction.getTransactionStatus());

        assertEquals(1, testAccountService.getBalance(accountId1), 0.00001);
        assertEquals(10, testAccountService.getBalance(accountId2), 0.00001);

    }
}