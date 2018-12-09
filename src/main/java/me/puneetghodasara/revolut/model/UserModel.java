package me.puneetghodasara.revolut.model;

import me.puneetghodasara.revolut.entity.UserEntity;

import java.io.Serializable;
import java.util.Set;

/**
 * This class models the User and can be transferred </br>
 * from backend to frontend with JSON serializer.
 */
public class UserModel implements Serializable {

    private final String userId;
    private final Set<String> accountIds;

    public UserModel(final String userId, final Set<String> accountIds) {
        this.userId = userId;
        this.accountIds = accountIds;
    }

    public String getUserId() {
        return userId;
    }

    public Set<String> getAccountIds() {
        return accountIds;
    }

    public static final UserModel fromEntity(final UserEntity userEntity){
        final UserModel userModel = new UserModel(userEntity.getUserId(), userEntity.getAccounts());
        return userModel;
    }
}
