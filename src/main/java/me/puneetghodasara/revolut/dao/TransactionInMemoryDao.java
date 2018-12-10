package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.Transaction;

import java.util.Optional;

public class TransactionInMemoryDao extends InMemoryDao<String, Transaction> implements TransactionRepository {

    @Override
    public void update(final Transaction transaction) {
        storage.put(transaction.getTransactionId(), transaction);
    }

    @Override
    public Optional<Transaction> get(final String transactionId) {
        return Optional.ofNullable(storage.get(transactionId));
    }
}
