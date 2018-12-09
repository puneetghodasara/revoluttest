package me.puneetghodasara.revolut.model;

import me.puneetghodasara.revolut.entity.AccountEntity;

import java.io.Serializable;

/**
 * This class models the Account and can be transferred </br>
 * from backend to frontend with JSON serializer.
 */
public class AccountModel implements Serializable {

    private final String accountId;

    private final String currencyCode;

    public AccountModel(final String accountId, final String currencyCode, final Double amount) {
        this.accountId = accountId;
        this.currencyCode = currencyCode;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public static final AccountModel fromEntity(final AccountEntity accountEntity){
        final AccountModel auserModel = new AccountModel(accountEntity.getAccountId(),
                accountEntity.getCurrency().getCurrencyCode(),
                accountEntity.getAmount());
        return auserModel;
    }
}
