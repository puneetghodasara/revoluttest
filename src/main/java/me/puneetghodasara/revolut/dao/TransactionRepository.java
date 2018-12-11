package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.Transaction;

import java.util.Optional;

public interface TransactionRepository {

    Transaction updateEntity(final String id, Transaction transaction);

    Optional<Transaction> getById(String transactionId);
}
