package me.puneetghodasara.revolut.model;

import me.puneetghodasara.revolut.entity.UserEntity;

import java.io.Serializable;

/**
 * This class models the User and can be transferred </br>
 * from backend to frontend with JSON serializer.
 */
public class UserModel implements Serializable {

    private final String userId;

    public UserModel(final String userId) {
        this.userId = userId;
    }

    public static final UserModel fromEntity(final UserEntity userEntity){
        final UserModel userModel = new UserModel(userEntity.getUserId());
        return userModel;
    }
}
