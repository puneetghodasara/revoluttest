package me.puneetghodasara.revolut.dao;

import me.puneetghodasara.revolut.entity.Transaction;

import java.util.Optional;

public class TransactionInMemoryDao extends InMemoryDao<String, Transaction> implements TransactionRepository {

}
