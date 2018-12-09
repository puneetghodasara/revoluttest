package me.puneetghodasara.revolut.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountNumberServiceImplTest {

    private AccountNumberServiceImpl testObject;

    @Before
    public void setUp() throws Exception {
        testObject = new AccountNumberServiceImpl();
    }

    @Test
    public void nextAccountNumber() {
        final String nextAccountNumber = testObject.nextAccountNumber();
        Assert.assertNotNull(nextAccountNumber);
        Assert.assertEquals("1", nextAccountNumber);
    }
}