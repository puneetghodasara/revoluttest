package me.puneetghodasara.revolut.endpoint;

import me.puneetghodasara.revolut.dao.AccountInMemoryDao;
import me.puneetghodasara.revolut.dao.UserInMemoryDao;
import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.entity.UserEntity;
import me.puneetghodasara.revolut.exception.AccountOperationException;
import me.puneetghodasara.revolut.exception.UserOperationException;
import me.puneetghodasara.revolut.model.AccountModel;
import me.puneetghodasara.revolut.model.UserModel;
import me.puneetghodasara.revolut.service.AccountNumberServiceImpl;
import me.puneetghodasara.revolut.service.AccountServiceImpl;
import me.puneetghodasara.revolut.service.UserServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Currency;
import java.util.Optional;

import static org.junit.Assert.*;

public class UserAccountEndpointImplTest {

    private UserAccountEndpointImpl testObject;
    private UserInMemoryDao userRepository;
    private AccountInMemoryDao accountRepository;

    @Before
    public void setUp() throws Exception {
        // All parameters should be mocked
        userRepository = new UserInMemoryDao();
        accountRepository = new AccountInMemoryDao();
        testObject = new UserAccountEndpointImpl(new UserServiceImpl(userRepository, new AccountServiceImpl(accountRepository, new AccountNumberServiceImpl())));
    }

    @Test
    public void getAllUsers() {
        userRepository.updateEntity("mockUser-1", new UserEntity("mockUser-1"));
        userRepository.updateEntity("mockUser-2", new UserEntity("mockUser-2"));
        final long totalUsers = testObject.getAllUsers().count();
        Assert.assertEquals(2, totalUsers);
    }

    @Test
    public void getUserFalse() {
        userRepository.updateEntity("mockUser-1", new UserEntity("mockUser-1"));
        final Optional<UserModel> user = testObject.getUser("mockUser-2");
        Assert.assertNotNull(user);
        Assert.assertFalse(user.isPresent());
    }

    @Test
    public void getUserTrue() {
        userRepository.updateEntity("mockUser-1", new UserEntity("mockUser-1"));
        final Optional<UserModel> user = testObject.getUser("mockUser-1");
        Assert.assertNotNull(user);
        Assert.assertTrue(user.isPresent());
        Assert.assertEquals("mockUser-1", user.get().getUserId());
    }

    @Test
    public void createUser() throws UserOperationException {
        testObject.createUser("mockUser-1");
        final long count = userRepository.getAll().count();
        Assert.assertEquals(1, count);
    }

    @Test(expected = UserOperationException.class)
    public void createDuplicateUser() throws UserOperationException {
        userRepository.updateEntity("mockUser-1", new UserEntity("mockUser-1"));
        testObject.createUser("mockUser-1");
    }

    @Test
    public void deleteUser() throws AccountOperationException, UserOperationException {
        userRepository.updateEntity("mockUser-1", new UserEntity("mockUser-1"));
        final boolean deleted = testObject.deleteUser("mockUser-1");
        Assert.assertTrue(deleted);
    }

    @Test(expected = AccountOperationException.class)
    public void deleteUserWithNonZeroAccount() throws AccountOperationException, UserOperationException {
        accountRepository.updateEntity("mockAccount-1", new AccountEntity("mockAccount-1", Currency.getInstance("EUR")).withNewAmount(1D));
        userRepository.updateEntity("mockUser-1", new UserEntity("mockUser-1").withNewAccount("mockAccount-1"));
        final boolean deleted = testObject.deleteUser("mockUser-1");
    }

    @Test
    public void getAccounts() throws UserOperationException {
        accountRepository.updateEntity("mockAccount-1", new AccountEntity("mockAccount-1", Currency.getInstance("EUR")));
        accountRepository.updateEntity("mockAccount-2", new AccountEntity("mockAccount-2", Currency.getInstance("EUR")));
        accountRepository.updateEntity("mockAccount-3", new AccountEntity("mockAccount-3", Currency.getInstance("EUR")));
        userRepository.updateEntity("mockUser-1", new UserEntity("mockUser-1").withNewAccount("mockAccount-1").withNewAccount("mockAccount-2"));


        final int accounts = testObject.getAccounts("mockUser-1").size();
        Assert.assertEquals(2, accounts);
    }

    @Test
    public void getAccount() throws AccountOperationException, UserOperationException {
        accountRepository.updateEntity("mockAccount-1", new AccountEntity("mockAccount-1", Currency.getInstance("EUR")));
        userRepository.updateEntity("mockUser-1", new UserEntity("mockUser-1").withNewAccount("mockAccount-1"));

        final AccountModel account = testObject.getAccount("mockUser-1", "mockAccount-1");
        Assert.assertNotNull(account);
        Assert.assertEquals("mockAccount-1", account.getAccountId());
        Assert.assertEquals("EUR", account.getCurrencyCode());
    }

    @Test(expected = AccountOperationException.class)
    public void getCrossAccount() throws AccountOperationException, UserOperationException {
        accountRepository.updateEntity("mockAccount-1", new AccountEntity("mockAccount-1", Currency.getInstance("EUR")));
        userRepository.updateEntity("mockUser-1", new UserEntity("mockUser-1").withNewAccount("mockAccount-1"));

        accountRepository.updateEntity("mockAccount-2", new AccountEntity("mockAccount-2", Currency.getInstance("EUR")));
        userRepository.updateEntity("mockUser-2", new UserEntity("mockUser-2").withNewAccount("mockAccount-2"));

        final AccountModel account = testObject.getAccount("mockUser-1", "mockAccount-2");
    }

    @Test
    public void openNewAccount() throws AccountOperationException, UserOperationException {
        userRepository.updateEntity("mockUser-1", new UserEntity("mockUser-1"));
        testObject.openNewAccount("mockUser-1", "EUR");
        final long count = accountRepository.getAll().count();
        assertEquals(1, count);
    }

    @Test(expected = IllegalArgumentException.class)
    public void openNewAccountWithFakeCurrency() throws AccountOperationException, UserOperationException {
        testObject.openNewAccount("mockUser-1", "DUM");
    }

    @Test
    public void deleteAccount() throws AccountOperationException, UserOperationException {
        accountRepository.updateEntity("mockAccount-1", new AccountEntity("mockAccount-1", Currency.getInstance("EUR")));
        accountRepository.updateEntity("mockAccount-2", new AccountEntity("mockAccount-2", Currency.getInstance("EUR")));
        userRepository.updateEntity("mockUser-1", new UserEntity("mockUser-1").withNewAccount("mockAccount-1").withNewAccount("mockAccount-2"));

        testObject.deleteAccount("mockUser-1", "mockAccount-2");
        final long count = accountRepository.getAll().count();
        assertEquals(1, count);
    }
}