package me.puneetghodasara.revolut.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A UserEntity class that holds all the user information </br>
 * It is made immutable for multi-threading use
 *
 */
public class UserEntity {

    private final String userId;

    private final Set<AccountEntity> accounts;

    public UserEntity(final String userId) {
        this(userId, new HashSet<>());
    }

    /**
     * private because User would be registered without accounts only
     */
    private UserEntity(final String userId, final Set<AccountEntity> accounts) {
        this.userId = userId;
        this.accounts = accounts;
    }


    public UserEntity withNewAccount(final AccountEntity newAccount){
        final Set<AccountEntity> originalAccounts = new HashSet<>(this.accounts);
        originalAccounts.add(newAccount);
        return new UserEntity(this.userId, originalAccounts);
    }

    /** Getters Setters **/
    public String getUserId() {
        return userId;
    }

    public Set<AccountEntity> getAccounts() {
        return Collections.unmodifiableSet(accounts);
    }

}