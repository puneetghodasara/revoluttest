package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.AccountEntity;

import java.util.Map;
import java.util.stream.Stream;

/**
 * An In-Memory implementation of Account Repository </br>
 * This class uses HashMap based storage.
 */
public class AccountInMemoryDao extends InMemoryDao<String, AccountEntity> implements AccountRepository {

}
