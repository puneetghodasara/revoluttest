package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.AccountEntity;

import java.util.Optional;
import java.util.stream.Stream;

public interface AccountRepository {

    Optional<AccountEntity> getAccount(final String id);

    void updateAccount(final AccountEntity accountEntity);

    Stream<AccountEntity> getAllAccounts();

    void deleteAccount(String accountId);
}
