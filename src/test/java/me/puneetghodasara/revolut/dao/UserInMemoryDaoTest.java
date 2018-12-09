package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.entity.UserEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Currency;
import java.util.Optional;

public class UserInMemoryDaoTest {

    private UserInMemoryDao testObject;
    private UserEntity actualObject;

    @Before
    public void setUp() throws Exception {
        testObject = new UserInMemoryDao();
        actualObject = new UserEntity("mockUser-1");
        testObject.storage.put("mockUser-1", actualObject);
    }

    @Test
    public void getUser() {
        final Optional<UserEntity> answerObject = testObject.getUser("mockUser-1");
        Assert.assertTrue(answerObject.isPresent());
        Assert.assertEquals(answerObject.get(), actualObject);

    }

    @Test
    public void updateUser() {

        final UserEntity actualObject = testObject.getUser("mockUser-1")
                .orElseThrow(AssertionError::new)
                .withNewAccount(new AccountEntity("dummyAccount-1", Currency.getInstance("EUR")));

        testObject.updateUser(actualObject);

        final Optional<UserEntity> answerObject = testObject.getUser("mockUser-1");

        Assert.assertTrue(answerObject.isPresent());
        Assert.assertNotNull(answerObject.get().getAccounts());
        Assert.assertEquals(1, answerObject.get().getAccounts().size());
    }
}