package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.UserEntity;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserRepository {

    Stream<UserEntity> getAll();

    Optional<UserEntity> getById(final String id);

    UserEntity updateEntity(final String id, final UserEntity userEntity);

    void deleteById(String userId);
}
