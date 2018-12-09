package me.puneetghodasara.revolut.service;

import me.puneetghodasara.revolut.dao.UserInMemoryDao;
import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.entity.UserEntity;
import me.puneetghodasara.revolut.exception.UserOperationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Currency;

public class UserServiceImplTest {

    private UserServiceImpl testObject;
    private UserInMemoryDao userRepository;

    @Before
    public void setUp() throws Exception {
        userRepository = new UserInMemoryDao();
        testObject = new UserServiceImpl(userRepository);
    }

    @Test
    public void register() throws UserOperationException {
        testObject.register("mockUser-1");
        final UserEntity answerObject = userRepository.getUser("mockUser-1").orElseThrow(AssertionError::new);
        Assert.assertNotNull(answerObject);
        Assert.assertEquals("mockUser-1", answerObject.getUserId());
    }

    @Test
    public void addAccount() throws UserOperationException {
        testObject.register("mockUser-1");
        UserEntity userEntity = userRepository.getUser("mockUser-1").orElseThrow(AssertionError::new);
        userEntity = testObject.addAccount(userEntity, new AccountEntity("mockAccount-1", Currency.getInstance("EUR")));
        userEntity = testObject.addAccount(userEntity, new AccountEntity("mockAccount-2", Currency.getInstance("USD")));

        Assert.assertNotNull(userRepository.getUser("mockUser-1").get().getAccounts());
        Assert.assertEquals(2, userRepository.getUser("mockUser-1").get().getAccounts().size());

    }
}