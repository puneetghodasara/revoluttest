package me.puneetghodasara.revolut.model;

import me.puneetghodasara.revolut.entity.AccountEntity;
import me.puneetghodasara.revolut.entity.UserEntity;

import java.io.Serializable;
import java.util.Currency;

/**
 * This class models the Account and can be transferred </br>
 * from backend to frontend with JSON serializer.
 */
public class AccountModel implements Serializable {

    private final String accountId;

    private final String currencyCode;

    private final Double amount;

    public AccountModel(final String accountId, final String currencyCode, final Double amount) {
        this.accountId = accountId;
        this.currencyCode = currencyCode;
        this.amount = amount;
    }

    public static final AccountModel fromEntity(final AccountEntity accountEntity){
        final AccountModel auserModel = new AccountModel(accountEntity.getAccountId(),
                accountEntity.getCurrency().getCurrencyCode(),
                accountEntity.getAmount());
        return auserModel;
    }
}
