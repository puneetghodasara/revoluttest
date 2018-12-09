package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.UserEntity;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserRepository {

    Stream<UserEntity> getUsers();

    Optional<UserEntity> getUser(final String id);

    UserEntity updateUser(final UserEntity userEntity);

    void deleteUser(String userId);
}
