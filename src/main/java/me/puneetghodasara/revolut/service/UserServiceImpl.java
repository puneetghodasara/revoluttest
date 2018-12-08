package me.puneetghodasara.revolut.service;

import me.puneetghodasara.revolut.dao.UserRepository;
import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.entity.UserEntity;
import me.puneetghodasara.revolut.exception.UserOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Method is synchronized to prevent race condition
     */
    @Override
    public synchronized UserEntity register(final String userId) throws UserOperationException {
        final boolean present = userRepository.getUser(userId).isPresent();
        if (present) {
            logger.warn("User ID {} was already registered ", userId);
            throw new UserOperationException(UserOperationException.UserOperationExceptionMessages.USERID_TAKEN);
        }

        return userRepository.updateUser(new UserEntity(userId));
    }

    @Override
    public void addAccount(final UserEntity userEntity, final AccountEntity accountEntity) {
        userEntity.withNewAccount(accountEntity);
    }
}
