package me.puneetghodasara.revolut.endpoint;

import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.exception.AccountOperationException;
import me.puneetghodasara.revolut.exception.UserOperationException;
import me.puneetghodasara.revolut.model.AccountModel;
import me.puneetghodasara.revolut.model.UserModel;
import me.puneetghodasara.revolut.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An Implementation of {@link UserAccountEndpoint} REST API calls
 */
public class UserAccountEndpointImpl implements UserAccountEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(UserAccountEndpointImpl.class);

    private final UserService userService;

    public UserAccountEndpointImpl(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public Stream<UserModel> getAllUsers() {
        return userService.getUsers()
                .map(UserModel::fromEntity);
    }

    @Override
    public Optional<UserModel> getUser(final String userId) {
        return userService.getUser(userId)
                .map(UserModel::fromEntity);
    }

    @Override
    public UserModel createUser(final String userId) throws UserOperationException {
        userService.register(userId);
        return findUserOrThrow(userId);
    }

    @Override
    public boolean deleteUser(final String userId) throws UserOperationException, AccountOperationException {
        return userService.unregister(userId);
    }

    @Override
    public List<AccountModel> getAccounts(final String userId) throws UserOperationException {
        final Set<String> accounts = userService.getUser(userId)
                .orElseThrow(() -> new UserOperationException(UserOperationException.UserOperationExceptionMessages.ERROR_REGISTERING_USER))
                .getAccounts();

        return accounts.stream()
                .map(acc -> {
                    try {
                        return Optional.of(getAccount(userId, acc));
                    } catch (AccountOperationException | UserOperationException e) {
                        return Optional.ofNullable((AccountModel) null);
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public AccountModel getAccount(final String userId, final String accountId) throws AccountOperationException, UserOperationException {
        return AccountModel.fromEntity(userService.getUserAccount(userId, accountId));
    }

    @Override
    public AccountModel openNewAccount(final String userId, final String currencyCode) throws UserOperationException, AccountOperationException {
        final Currency currency;
        try {
            currency = Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Currency code {} provided", currencyCode);
            throw e;
        }

        final AccountEntity accountEntity = userService.addAccount(userId, currency);
        return AccountModel.fromEntity(accountEntity);
    }

    @Override
    public boolean deleteAccount(final String userId, final String accountId) throws AccountOperationException, UserOperationException {
        userService.removeAccount(userId, accountId);
        return true;
    }

    private UserModel findUserOrThrow(final String userId) throws UserOperationException {
        return getUser(userId)
                .orElseThrow(() -> new UserOperationException(UserOperationException.UserOperationExceptionMessages.ERROR_REGISTERING_USER));
    }
}
