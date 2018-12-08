package me.puneetghodasara.revolut.service;

/**
 * This is a service that returns next valid account number
 */
@FunctionalInterface
public interface AccountNumberService {

    /**
     * returns next account number
     * @return
     */
    String nextAccountNumber();
}
