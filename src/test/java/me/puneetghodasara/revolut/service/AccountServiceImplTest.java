package me.puneetghodasara.revolut.service;

import me.puneetghodasara.revolut.dao.AccountInMemoryDao;
import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.exception.AccountOperationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Currency;

public class AccountServiceImplTest {

    // Double requires precision comparision, hence we would take 1/10000 of currency unit
    private static final double DELTA = 0.0001;
    private AccountServiceImpl testObject;
    private AccountInMemoryDao accountRepository;

    @Before
    public void setUp() throws Exception {
        // passed parameter should be mocked
        accountRepository = new AccountInMemoryDao();
        testObject = new AccountServiceImpl(accountRepository, new AccountNumberServiceImpl());
    }

    @Test
    public void open() throws AccountOperationException {
        testObject.open(Currency.getInstance("EUR"));
        final long totalAccounts = accountRepository.getAllAccounts().count();
        Assert.assertEquals(1, totalAccounts);
    }

    @Test
    public void credit() throws AccountOperationException {
        final AccountEntity accountEntity = testObject.open(Currency.getInstance("EUR"));
        final boolean credit = testObject.credit(accountEntity, 10.25);
        Assert.assertTrue(credit);
        final Double answer = testObject.getBalance(accountEntity);
        Assert.assertEquals(10.25, answer, DELTA);
    }


    @Test
    public void debit_Insufficient() throws AccountOperationException {
        final AccountEntity accountEntity = testObject.open(Currency.getInstance("EUR"));
        final boolean debited = testObject.debit(accountEntity, 1D);
        Assert.assertFalse(debited);
    }

    @Test
    public void debit_Success() throws AccountOperationException {
        final AccountEntity accountEntity = testObject.open(Currency.getInstance("EUR"));
        testObject.credit(accountEntity, 10D);
        final boolean debited = testObject.debit(accountEntity, 1D);
        Assert.assertTrue(debited);

        final Double balance = testObject.getBalance(accountEntity);
        Assert.assertNotNull(balance);
        Assert.assertEquals(9D, balance, DELTA);
    }

    @Test
    public void getBalance() throws AccountOperationException {
        final AccountEntity accountEntity = testObject.open(Currency.getInstance("EUR"));
        Assert.assertEquals(0, testObject.getBalance(accountEntity), DELTA);

        testObject.credit(accountEntity, 10D);
        Assert.assertEquals(10, testObject.getBalance(accountEntity), DELTA);

        testObject.credit(accountEntity, 0.1D);
        Assert.assertEquals(10.1, testObject.getBalance(accountEntity), DELTA);

        testObject.debit(accountEntity, 0.8D);
        Assert.assertEquals(9.3, testObject.getBalance(accountEntity), DELTA);

    }
}