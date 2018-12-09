package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.AccountEntity;
import org.junit.Assert;
import org.junit.Before;

import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AccountInMemoryDaoTest {

    private AccountInMemoryDao testObject;
    private AccountEntity actualObject;

    @Before
    public void setUp() throws Exception {
        testObject = new AccountInMemoryDao();
        actualObject = new AccountEntity("mockAccount-1", Currency.getInstance("EUR"), 0d);
        testObject.storage.put("mockAccount-1",
                actualObject);

        testObject.storage.put("mockAccount-2",
                new AccountEntity("mockAccount-2", Currency.getInstance("USD")));
        testObject.storage.put("mockAccount-3",
                new AccountEntity("mockAccount-3", Currency.getInstance("USD")));


    }

    @org.junit.Test
    public void getAccount() {
        final Optional<AccountEntity> answerObject = testObject.getAccount("mockAccount-1");

        Assert.assertTrue(answerObject.isPresent());
        Assert.assertEquals(answerObject.get(), actualObject);

    }

    @org.junit.Test
    public void updateAccount() {
        actualObject = new AccountEntity("mockAccount-1", Currency.getInstance("EUR"), 10d);
        testObject.updateAccount(actualObject);

        final Optional<AccountEntity> answerObject = testObject.getAccount("mockAccount-1");

        Assert.assertTrue(answerObject.isPresent());
        Assert.assertEquals(answerObject.get().getAmount(), Double.valueOf(10d));

    }

    @org.junit.Test
    public void getAccountsByCurrency() {

        final List<AccountEntity> accountsByCurrency = testObject.getAllAccounts()
                .collect(Collectors.toList());

        Assert.assertNotNull(accountsByCurrency);
        Assert.assertEquals(accountsByCurrency.size(), 3);

    }
}