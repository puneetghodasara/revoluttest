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
    public void testOpen() throws AccountOperationException {
        final AccountEntity account = testObject.open(Currency.getInstance("EUR"));
        Assert.assertNotNull(account);
        Assert.assertEquals("EUR", account.getCurrency().getCurrencyCode());
        final long totalAccounts = accountRepository.getAllAccounts().count();
        Assert.assertEquals(1, totalAccounts);
    }

    @Test
    public void testCredit() throws AccountOperationException {
        final AccountEntity account = testObject.open(Currency.getInstance("EUR"));
        final boolean credit = testObject.credit(account.getAccountId(), 10.25);
        Assert.assertTrue(credit);
        final Double answer = testObject.getBalance(account.getAccountId());
        Assert.assertEquals(10.25, answer, DELTA);
    }


    @Test
    public void debit_Insufficient() throws AccountOperationException {
        final AccountEntity accountEntity = testObject.open(Currency.getInstance("EUR"));
        final boolean debited = testObject.debit(accountEntity.getAccountId(), 1D);
        Assert.assertFalse(debited);
    }

    @Test
    public void debit_Success() throws AccountOperationException {
        final AccountEntity accountEntity = testObject.open(Currency.getInstance("EUR"));
        testObject.credit(accountEntity.getAccountId(), 10D);
        final boolean debited = testObject.debit(accountEntity.getAccountId(), 1D);
        Assert.assertTrue(debited);

        final Double balance = testObject.getBalance(accountEntity.getAccountId());
        Assert.assertNotNull(balance);
        Assert.assertEquals(9D, balance, DELTA);
    }

    @Test
    public void getBalance() throws AccountOperationException {
        final String accountId = testObject.open(Currency.getInstance("EUR")).getAccountId();
        Assert.assertEquals(0, testObject.getBalance(accountId), DELTA);

        testObject.credit(accountId, 10D);
        Assert.assertEquals(10, testObject.getBalance(accountId), DELTA);

        testObject.credit(accountId, 0.1D);
        Assert.assertEquals(10.1, testObject.getBalance(accountId), DELTA);

        testObject.debit(accountId, 0.8D);
        Assert.assertEquals(9.3, testObject.getBalance(accountId), DELTA);

    }

    @Test
    public void testDeleteAccount() throws AccountOperationException {
        final String accountId = testObject.open(Currency.getInstance("EUR")).getAccountId();
        final boolean deleted = testObject.deleteAccount(accountId);
        Assert.assertTrue(deleted);
    }

    @Test(expected = AccountOperationException.class)
    public void testDeleteAccountNonZeroBalance() throws AccountOperationException {
        final String accountId = testObject.open(Currency.getInstance("EUR")).getAccountId();
        testObject.credit(accountId, 2D);
        final boolean deleted = testObject.deleteAccount(accountId);
        Assert.assertFalse(deleted);

    }
}