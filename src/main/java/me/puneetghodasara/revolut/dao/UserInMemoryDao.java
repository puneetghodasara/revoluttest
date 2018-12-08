package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.UserEntity;

import java.util.Map;
import java.util.Optional;

/**
 * An In-Memory implementation of User Repository </br>
 * This class uses HashMap based storage.
 */
public class UserInMemoryDao extends InMemoryDao<String, UserEntity> implements UserRepository {

    @Override
    public Optional<UserEntity> getUser(final String id) {
        return storage.entrySet()
                .stream()
                .filter(record -> record.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findAny();
    }
}
