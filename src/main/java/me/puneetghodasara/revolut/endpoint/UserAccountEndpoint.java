package me.puneetghodasara.revolut.endpoint;

import me.puneetghodasara.revolut.exception.AccountOperationException;
import me.puneetghodasara.revolut.exception.UserOperationException;
import me.puneetghodasara.revolut.model.AccountModel;
import me.puneetghodasara.revolut.model.UserModel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * This is REST-ful endpoint of entity UserEntity.
 *
 * It can be treated or converted to Spring based endpoint.
 *
 * @Path("/user")
 */
public interface UserAccountEndpoint {

    /**
     * GET call to get all users
     * @Path("/user")
     * @return
     */
    Stream<UserModel> getAllUsers();

    /**
     * GET call to get User info
     * @Path("/user/<user-id>")
     * @param userId
     * @return
     */
    Optional<UserModel> getUser(final String userId);

    /**
     * POST call to create User
     * @Path("/user")
     * @return User
     */
    UserModel createUser(final String userId) throws UserOperationException;


    /**
     * DELTE call to delete User
     * @Path("/user/<user-id>")
     * @param userId
     * @return
     */
    boolean deleteUser(final String userId) throws UserOperationException, AccountOperationException;


    /**
     * GET call to get accouns of a User
     * @Path("/user/<user-id>/account")
     * @param userId
     * @return
     */
    List<AccountModel> getAccounts(final String userId) throws UserOperationException;

    /**
     * GET call to get an accoun of a User
     * @Path("/user/<user-id>/account/<account-id>")
     * @param userId
     * @return
     */
    AccountModel getAccount(final String userId, final String accountId) throws UserOperationException, AccountOperationException;

    /**
     * POST add account to User
     * @Path("/user/<user-id>/account")
     * @param userId
     * @param currencyCode
     * @return
     */
    AccountModel openNewAccount(final String userId, final String currencyCode) throws UserOperationException, AccountOperationException;

    /**
     * DELETE remove account from User
     * @Path("/user/<user-id>/account/<account-id>")
     * @param userId
     * @return
     */
    boolean deleteAccount(final String userId, final String accountId) throws AccountOperationException, UserOperationException;


}
