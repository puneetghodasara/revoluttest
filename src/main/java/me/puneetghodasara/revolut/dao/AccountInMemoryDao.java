package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.AccountEntity;

import java.util.Currency;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * An In-Memory implementation of Account Repository </br>
 * This class uses HashMap based storage.
 */
public class AccountInMemoryDao extends InMemoryDao<String, AccountEntity> implements AccountRepository {

    @Override
    public Optional<AccountEntity> getAccount(final String id) {
        return storage.entrySet()
                .stream()
                .filter(record -> record.getKey().equals(id))
                .map(Map.Entry::getValue)
                .findAny();
    }

    @Override
    public void updateAccount(final AccountEntity accountEntity) {
        storage.put(accountEntity.getAccountId(), accountEntity);
    }

    @Override
    public Stream<AccountEntity> getAllAccounts() {
        return storage.entrySet()
                .stream()
//                .filter(record -> record.getValue().getCurrency().getNumericCode() == currency.getNumericCode())
                .map(Map.Entry::getValue);

    }
}
