package me.puneetghodasara.revolut.service;

import me.puneetghodasara.revolut.dao.AccountInMemoryDao;
import me.puneetghodasara.revolut.dao.AccountRepository;
import me.puneetghodasara.revolut.entity.Transaction;
import me.puneetghodasara.revolut.entity.TransactionStatus;
import me.puneetghodasara.revolut.exception.AccountOperationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Currency;

import static org.junit.Assert.assertEquals;

public class TransactionProcessorTest {

    private TransactionQueueBroker testTxBroker;
    private TransactionProcessor testTxProcessor;
    private TransactionProcessor testErrorTxProcessor1;
    private TransactionProcessor testErrorTxProcessor2;
    private AccountService testAccountService;
    private AccountService testErrorAccountService1;
    private AccountService testErrorAccountService2;

    // Can be mocked
    class ErrorenousAccountServiceImpl extends AccountServiceImpl {


        private final int TOTAL_ATTEMPT_FAIL;

        public ErrorenousAccountServiceImpl(final AccountRepository accountRepository, final AccountNumberService accountNumberService, final int num) {
            super(accountRepository, accountNumberService);
            TOTAL_ATTEMPT_FAIL = TransactionProcessor.MAX_TRY + num;
        }

        private int attempt = 0;

        @Override
        public boolean credit(final String accountId, final Double creditAmount) throws AccountOperationException {

            if (attempt++ < TOTAL_ATTEMPT_FAIL) {
                throw new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.CREDIT_FAILED);
            } else {
                return super.credit(accountId, creditAmount);
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        // All parameters can be mocked
        testTxBroker = new TransactionQueueBroker();
        AccountInMemoryDao accountRepository = new AccountInMemoryDao();
        testAccountService = new AccountServiceImpl(accountRepository, new AccountNumberServiceImpl());
        SimpleConversionServiceImpl conversionService = new SimpleConversionServiceImpl();
        testTxProcessor = new TransactionProcessor(testTxBroker, testAccountService, conversionService, true);
        testErrorAccountService1 = new ErrorenousAccountServiceImpl(accountRepository, new AccountNumberServiceImpl(), 1);
        testErrorAccountService2 = new ErrorenousAccountServiceImpl(accountRepository, new AccountNumberServiceImpl(), 0);
        testErrorTxProcessor1 = new TransactionProcessor(testTxBroker, testErrorAccountService1, conversionService, true);
        testErrorTxProcessor2 = new TransactionProcessor(testTxBroker, testErrorAccountService2, conversionService, true);
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
    public void testMultiple() throws AccountOperationException {
        final String accountId1 = testAccountService.open(Currency.getInstance("EUR")).getAccountId();
        testAccountService.credit(accountId1, 10D);
        final String accountId2 = testAccountService.open(Currency.getInstance("EUR")).getAccountId();
        final Transaction transaction1 = new Transaction.Builder(accountId1, accountId2, 2D).build();
        testTxBroker.addTransaction(transaction1);
        final Transaction transaction2 = new Transaction.Builder(accountId1, accountId2, 2D).build();
        testTxBroker.addTransaction(transaction2);
        testTxProcessor.run();
        testTxProcessor.run();

        Assert.assertEquals(TransactionStatus.SUCCESS, transaction1.getTransactionStatus());
        Assert.assertEquals(TransactionStatus.SUCCESS, transaction2.getTransactionStatus());

        assertEquals(6, testAccountService.getBalance(accountId1), 0.00001);
        assertEquals(4, testAccountService.getBalance(accountId2), 0.00001);
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
        testErrorTxProcessor1.run();

        Assert.assertEquals(TransactionStatus.ERROR, transaction.getTransactionStatus());

        assertEquals(1, testAccountService.getBalance(accountId1), 0.00001);
        assertEquals(0, testAccountService.getBalance(accountId2), 0.00001);

        // Re-Add manually and in Second run it should pass
        testTxBroker.addTransaction(transaction);
        testTxProcessor.run();

        Assert.assertEquals(TransactionStatus.SUCCESS, transaction.getTransactionStatus());

        assertEquals(1, testAccountService.getBalance(accountId1), 0.00001);
        assertEquals(10, testAccountService.getBalance(accountId2), 0.00001);

    }

    @Test
    public void testRefund() throws AccountOperationException {
        final String accountId1 = testAccountService.open(Currency.getInstance("EUR")).getAccountId();
        testAccountService.credit(accountId1, 11D);
        final String accountId2 = testAccountService.open(Currency.getInstance("EUR")).getAccountId();
        final Transaction transaction = new Transaction.Builder(accountId1, accountId2, 10D).build();
        testTxBroker.addTransaction(transaction);

        // Run with error so it will be debited but not credited
        testErrorTxProcessor2.run();

        Assert.assertEquals(TransactionStatus.SUCCESS, transaction.getTransactionStatus());

        assertEquals(11, testAccountService.getBalance(accountId1), 0.00001);
        assertEquals(0, testAccountService.getBalance(accountId2), 0.00001);

    }


    @Test
    public void testIntraCurrencyOperation() throws AccountOperationException {
        final String accountId1 = testAccountService.open(Currency.getInstance("EUR")).getAccountId();
        testAccountService.credit(accountId1, 11D);
        final String accountId2 = testAccountService.open(Currency.getInstance("USD")).getAccountId();
        final Transaction transaction = new Transaction.Builder(accountId1, accountId2, 10D).build();
        testTxBroker.addTransaction(transaction);

        testTxProcessor.run();

        Assert.assertEquals(TransactionStatus.SUCCESS, transaction.getTransactionStatus());

        assertEquals(1, testAccountService.getBalance(accountId1), 0.00001);
        assertEquals(11.4, testAccountService.getBalance(accountId2), 0.00001);

    }
}