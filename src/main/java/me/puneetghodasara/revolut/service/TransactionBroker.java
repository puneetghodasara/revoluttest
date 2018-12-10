package me.puneetghodasara.revolut.service;

import me.puneetghodasara.revolut.entity.Transaction;

/**
 * Acts as a broker between transactions </br>
 *
 * It acts as an interface between various channels of creating </br>
 * transactions such as Mobile, PhoneBanking, Command etc </br>
 * and various transaction processors such as </br>
 * InterBank, IntraBank etc </br>
 */
public interface TransactionBroker {

    /**
     * Registers a transaction for to-be-processed
     * @param transaction
     * @return true if it is successfully accepted
     */
    boolean addTransaction(final Transaction transaction);

    /**
     * Pulls a next eligible transaction from to-be-processed </br>
     * to process
     * @return Transaction to process
     */
    Transaction getNextTransaction();
}
