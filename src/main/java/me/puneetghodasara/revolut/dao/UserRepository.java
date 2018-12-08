package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.UserEntity;

import java.util.Optional;

public interface UserRepository {

    Optional<UserEntity> getUser(final String id);

    UserEntity updateUser(final UserEntity userEntity);

}
