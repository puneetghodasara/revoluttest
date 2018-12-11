package me.puneetghodasara.revolut.service;

import me.puneetghodasara.revolut.dao.UserRepository;
import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.entity.UserEntity;
import me.puneetghodasara.revolut.exception.AccountOperationException;
import me.puneetghodasara.revolut.exception.UserOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Currency;
import java.util.Optional;
import java.util.stream.Stream;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final AccountService accountService;

    public UserServiceImpl(final UserRepository userRepository, final AccountService accountService) {
        this.userRepository = userRepository;
        this.accountService = accountService;
    }

    @Override
    public Stream<UserEntity> getUsers() {
        return userRepository.getAll();
    }

    @Override
    public Optional<UserEntity> getUser(final String userId) {
        return userRepository.getById(userId);
    }

    /**
     * Method is synchronized to prevent race condition
     */
    @Override
    public synchronized void register(final String userId) throws UserOperationException {
        final boolean present = userRepository.getById(userId).isPresent();
        if (present) {
            logger.warn("User ID {} was already registered ", userId);
            throw new UserOperationException(UserOperationException.UserOperationExceptionMessages.USERID_TAKEN);
        }

        userRepository.updateEntity(userId, new UserEntity(userId));
    }

    @Override
    public boolean unregister(final String userId) throws UserOperationException, AccountOperationException {
        final UserEntity user = findUserOrThrow(userId);
        if (user.getAccounts().stream()
                .map(accountService::getAccount)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(acc -> acc.getAmount() > 0D)) {
            logger.error("Unable to delete User {} as it has non empty accounts", userId);
            throw new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.NONEMPTY_ACCOUNTS);
        }

        final boolean errorDeleted = user.getAccounts()
                .stream()
                .map(accountService::getAccount)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(acc -> {
                    try {
                        return accountService.deleteAccount(acc.getAccountId());
                    } catch (AccountOperationException e) {
                        return false;
                    }
                })
                .anyMatch(deleted -> !deleted);
        if(errorDeleted){
            logger.error("Unable to delete User {} as error in deleting one or more of the accounts", userId);
            throw new UserOperationException(UserOperationException.UserOperationExceptionMessages.ERROR_DELETEING_ACCOUNTS);
        }

        userRepository.deleteById(userId);
        return true;
    }

    @Override
    public AccountEntity addAccount(final String userId, final Currency currency) throws UserOperationException, AccountOperationException {
        final UserEntity user = findUserOrThrow(userId);
        final AccountEntity account = accountService.open(currency);
        final UserEntity userWithNewAccount = user.withNewAccount(account.getAccountId());
        userRepository.updateEntity(userId, userWithNewAccount);
        return account;
    }

    @Override
    public void removeAccount(final String userId, final String accountId) throws UserOperationException, AccountOperationException {
        final UserEntity user = findUserOrThrow(userId);

        if(!accountService.deleteAccount(accountId)){
            throw new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.ERROR_DELETEING_ACCOUNTS);
        }

        final UserEntity userWithoutAccount = user.withOutAccount(accountId);
        userRepository.updateEntity(userWithoutAccount.getUserId(), userWithoutAccount);
    }

    @Override
    public AccountEntity getUserAccount(final String userId, final String accountId) throws UserOperationException, AccountOperationException {
        final UserEntity user = findUserOrThrow(userId);
        if(user.getAccounts().contains(accountId)){
            return findAccountOrThrow(accountId);
        }
        throw new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.UNKNOWN_ACCOUNT_FOR_USER);
    }

    private UserEntity findUserOrThrow(final String userId) throws UserOperationException {
        return getUser(userId)
                .orElseThrow(() -> new UserOperationException(UserOperationException.UserOperationExceptionMessages.INVALID_USERID));
    }

    private AccountEntity findAccountOrThrow(final String accountId) throws AccountOperationException {
        return accountService.getAccount(accountId)
                .orElseThrow(() -> new AccountOperationException(AccountOperationException.AccountOperationExceptionMessages.UNKNOWN_ACCOUNT));
    }
}
