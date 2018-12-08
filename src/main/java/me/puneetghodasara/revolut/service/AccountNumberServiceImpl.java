package me.puneetghodasara.revolut.service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link AccountNumberService} implementation that uses running account number strategy. </br>
 * Ideally, it should use IBAN generator
 */
public class AccountNumberServiceImpl implements AccountNumberService {

    private final AtomicInteger accountNumberSequence = new AtomicInteger();

    @Override
    public String nextAccountNumber() {
        return String.valueOf(accountNumberSequence.addAndGet(1));
    }
}
