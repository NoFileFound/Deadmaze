package org.deadmaze.database;

// Imports
import static dev.morphia.query.experimental.filters.Filters.eq;
import org.deadmaze.database.collections.Account;
import java.util.List;

public class DBUtils {
    /**
     * Searches for account instance by given name.
     * @param nickname The given nickname.
     * @return The account instance if exist or else null.
     */
    public static Account findAccountByNickname(String nickname) {
        return DBManager.getDataStore().find(Account.class).filter(eq("playerName", nickname)).first();
    }

    /**
     * Searches for account instance by given name and password.
     * @param nickname The given nickname.
     * @param password The given password.
     * @return The account instance if exist or else null.
     */
    public static Account findAccountByPassword(String nickname, String password) {
        return DBManager.getDataStore().find(Account.class).filter(eq("playerName", nickname), eq("password", password)).first();
    }

    /**
     * Searches for all accounts that have the given email address.
     * @param email The given email address.
     * @return The accounts having the same email address.
     */
    public static List<Account> findAccountsByEmail(String email, String password) {
        if(password.isEmpty()) {
            return DBManager.getDataStore().find(Account.class).filter(eq("emailAddress", email)).stream().toList();
        }

        return DBManager.getDataStore().find(Account.class).filter(eq("emailAddress", email), eq("password", password)).stream().toList();
    }
}