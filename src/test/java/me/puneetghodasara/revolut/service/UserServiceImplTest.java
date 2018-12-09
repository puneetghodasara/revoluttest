package me.puneetghodasara.revolut.service;

import me.puneetghodasara.revolut.dao.AccountInMemoryDao;
import me.puneetghodasara.revolut.dao.UserInMemoryDao;
import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.entity.UserEntity;
import me.puneetghodasara.revolut.exception.AccountOperationException;
import me.puneetghodasara.revolut.exception.UserOperationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Currency;
import java.util.Optional;

public class UserServiceImplTest {

    private UserServiceImpl testObject;
    private UserInMemoryDao userRepository;
    private AccountInMemoryDao accountRepository;
    private AccountServiceImpl accountService;

    @Before
    public void setUp() throws Exception {
        userRepository = new UserInMemoryDao();
        // All parameters passed could be mocked
        accountRepository = new AccountInMemoryDao();
        accountService = new AccountServiceImpl(accountRepository, new AccountNumberServiceImpl());
        testObject = new UserServiceImpl(userRepository, accountService);
    }

    @Test
    public void testRegister() throws UserOperationException {
        testObject.register("mockUser-1");
        final UserEntity answerObject = userRepository.getUser("mockUser-1").orElseThrow(AssertionError::new);
        Assert.assertNotNull(answerObject);
        Assert.assertEquals("mockUser-1", answerObject.getUserId());
    }

    @Test(expected = UserOperationException.class)
    public void testRegisterWithSameUser() throws UserOperationException {
        testObject.register("mockUser-1");
        testObject.register("mockUser-1");
    }

    @Test
    public void testAddAccount() throws UserOperationException, AccountOperationException {
        testObject.register("mockUser-1");

        final AccountEntity answerObject1 = testObject.addAccount("mockUser-1", Currency.getInstance("EUR"));
        final AccountEntity answerObject2 = testObject.addAccount("mockUser-1", Currency.getInstance("USD"));
        Assert.assertNotNull(answerObject1);
        Assert.assertNotNull(answerObject2);
        Assert.assertEquals(2, testObject.getUser("mockUser-1").get().getAccounts().size());
    }

    @Test
    public void testGetUser() throws UserOperationException {
        testObject.register("mockUser-1");
        final Optional<UserEntity> answerObject = testObject.getUser("mockUser-1");
        Assert.assertNotNull(answerObject);
        Assert.assertTrue(answerObject.isPresent());
    }

    @Test
    public void testRemoveAccount() throws AccountOperationException, UserOperationException {
        testObject.register("mockUser-1");
        final String accountId = testObject.addAccount("mockUser-1", Currency.getInstance("EUR")).getAccountId();
        testObject.removeAccount("mockUser-1", accountId);
        Assert.assertEquals(0, testObject.getUser("mockUser-1").get().getAccounts().size());
    }

    @Test(expected = AccountOperationException.class)
    public void testRemoveAccountInvalidName() throws AccountOperationException, UserOperationException {
        testObject.register("mockUser-1");
        final String accountId = testObject.addAccount("mockUser-1", Currency.getInstance("EUR")).getAccountId();
        testObject.removeAccount("mockUser-1", "dummy");
        Assert.assertEquals(1, testObject.getUser("mockUser-1").get().getAccounts().size());
    }

    @Test(expected = UserOperationException.class)
    public void testUnregisterWithInvalid() throws UserOperationException, AccountOperationException {
        testObject.unregister("mockUser-1");
    }

    @Test
    public void testUnregister() throws UserOperationException, AccountOperationException {
        testObject.register("mockUser-1");
        testObject.unregister("mockUser-1");
        Assert.assertEquals(0, userRepository.getUsers().count());
    }

    @Test
    public void testUnregisterWithEmptyAccount() throws UserOperationException, AccountOperationException {
        testObject.register("mockUser-1");
        testObject.addAccount("mockUser-1", Currency.getInstance("EUR"));
        testObject.unregister("mockUser-1");
        Assert.assertEquals(0, userRepository.getUsers().count());
    }

    @Test(expected = AccountOperationException.class)
    public void testUnregisterWithnonEmptyAccount() throws UserOperationException, AccountOperationException {
        testObject.register("mockUser-1");
        final String accountId = testObject.addAccount("mockUser-1", Currency.getInstance("EUR")).getAccountId();
        accountService.credit(accountId, 1D);
        testObject.unregister("mockUser-1");
        Assert.assertEquals(0, userRepository.getUsers().count());
    }

    @Test
    public void testGetUserAccount() throws UserOperationException, AccountOperationException {
        testObject.register("mockUser-1");
        final String accountId = testObject.addAccount("mockUser-1", Currency.getInstance("EUR")).getAccountId();
        final AccountEntity account = testObject.getUserAccount("mockUser-1", accountId);
        Assert.assertNotNull(account);
        Assert.assertEquals("EUR", account.getCurrency().getCurrencyCode());
    }
}