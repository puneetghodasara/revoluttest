package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.Transaction;

import java.util.Optional;

public interface TransactionRepository {
    void update(Transaction transaction);

    Optional<Transaction> get(String transactionId);
}
