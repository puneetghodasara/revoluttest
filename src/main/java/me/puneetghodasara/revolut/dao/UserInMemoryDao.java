package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.UserEntity;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * An In-Memory implementation of User Repository </br>
 * This class uses HashMap based storage.
 */
public class UserInMemoryDao extends InMemoryDao<String, UserEntity> implements UserRepository {

    @Override
    public Stream<UserEntity> getUsers() {
        return storage.entrySet()
                .stream()
                .map(Map.Entry::getValue);

    }

    @Override
    public Optional<UserEntity> getUser(final String id) {
        return storage.entrySet()
                .stream()
                .filter(record -> record.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findAny();
    }

    @Override
    public UserEntity updateUser(final UserEntity userEntity) {
        storage.put(userEntity.getUserId(), userEntity);
        return storage.get(userEntity.getUserId());
    }

    @Override
    public void deleteUser(final String userId) {
        storage.remove(userId);
    }
}
