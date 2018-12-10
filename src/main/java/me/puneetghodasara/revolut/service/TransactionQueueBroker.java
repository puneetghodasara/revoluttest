package me.puneetghodasara.revolut.service;

import me.puneetghodasara.revolut.entity.Transaction;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This is very simplified transactionBroker that accepts </br>
 * {@link Transaction} in to a Queue </br>
 * </br>
 * For more productive scenario, this can be changed to </br>
 * Kafka or more sophisticated pub-sub topic based queue </br>
 */
public class TransactionQueueBroker implements TransactionBroker {

    private static final Queue<Transaction> queue = new LinkedBlockingQueue<>();

    @Override
    public boolean addTransaction(final Transaction transaction) {
        return queue.add(transaction);
    }

    @Override
    public Optional<Transaction> getNextTransaction() {
        return Optional.ofNullable(queue.poll());
    }
}
