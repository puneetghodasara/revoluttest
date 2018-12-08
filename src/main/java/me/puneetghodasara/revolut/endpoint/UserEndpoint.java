package me.puneetghodasara.revolut.endpoint;

import me.puneetghodasara.revolut.entity.UserEntity;
import me.puneetghodasara.revolut.model.UserModel;

/**
 * This is REST-ful endpoint of entity UserEntity.
 *
 * It can be treated or converted to Spring based endpoint.
 */
public interface UserEndpoint {

    /**
     * POST call to create UserEntity
     * @return UserEntity
     */
    UserModel createUser(final String userId);

}
