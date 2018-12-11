package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.AccountEntity;

import java.util.Optional;
import java.util.stream.Stream;

public interface AccountRepository {

    Optional<AccountEntity> getById(final String id);

    AccountEntity updateEntity(final String id, final AccountEntity accountEntity);

    Stream<AccountEntity> getAll();

    void deleteById(String accountId);
}
