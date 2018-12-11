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

}
