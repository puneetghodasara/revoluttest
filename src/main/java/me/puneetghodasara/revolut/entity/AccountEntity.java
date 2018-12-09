package me.puneetghodasara.revolut.entity;

import java.util.Currency;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * An account class holding information about accounts </br>
 * It is made immutable for multi-threading use
 */
public class AccountEntity {

    /**
     * An unique account ID. For Most European countries it can be IBAN and </br>
     * other countries it can be anything else.
     */
    private final String accountId;

    private final Currency currency;

    private final Double amount;

    private final ReadWriteLock amountLock;

    public AccountEntity(final String accountId, final Currency currency) {
        this(accountId, currency, 0d);
    }

    public AccountEntity(final String accountId, final Currency currency, final Double amount) {
        this.accountId = accountId;
        this.currency = currency;
        this.amount = amount;
        this.amountLock = new ReentrantReadWriteLock();
    }

    /**
     * To get a new object with all similar values except amount value
     * @param newAmountValue new amount value
     * @return new {@link AccountEntity} object with new amount value
     */
    public AccountEntity withNewAmount(final Double newAmountValue){
        return new AccountEntity(this.getAccountId(), this.getCurrency(), newAmountValue);
    }

    /**
     * Getters Setters below
     */

    public String getAccountId() {
        return accountId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Double getAmount() {
        return amount;
    }

    public ReadWriteLock getAmountLock() {
        return amountLock;
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final AccountEntity that = (AccountEntity) o;

        return accountId != null ? accountId.equals(that.accountId) : that.accountId == null;
    }

    @Override
    public int hashCode() {
        return accountId != null ? accountId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AccountEntity{" +
                "accountId='" + accountId + '\'' +
                '}';
    }
}
