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

    // Reference to primary key of account
    private final Set<String> accounts;

    public UserEntity(final String userId) {
        this(userId, Collections.EMPTY_SET);
    }

    /**
     * private because User would be registered without accounts only
     */
    private UserEntity(final String userId, final Set<String> accounts) {
        this.userId = userId;
        this.accounts = Collections.unmodifiableSet(accounts);
    }


    public UserEntity withNewAccount(final String newAccount){
        final Set<String> originalAccounts = new HashSet<>(this.accounts);
        originalAccounts.add(newAccount);
        return new UserEntity(this.userId, originalAccounts);
    }

    public UserEntity withOutAccount(final String newAccount){
        final Set<String> originalAccounts = new HashSet<>(this.accounts);
        originalAccounts.remove(newAccount);
        return new UserEntity(this.userId, originalAccounts);
    }

    /** Getters Setters **/
    public String getUserId() {
        return userId;
    }

    public Set<String> getAccounts() {
        return Collections.unmodifiableSet(accounts);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UserEntity that = (UserEntity) o;

        return userId != null ? userId.equals(that.userId) : that.userId == null;
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "userId='" + userId + '\'' +
                ", accounts=" + accounts +
                '}';
    }
}