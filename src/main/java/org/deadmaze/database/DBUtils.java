package org.deadmaze.database;

// Imports
import static dev.morphia.query.experimental.filters.Filters.elemMatch;
import static dev.morphia.query.experimental.filters.Filters.eq;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Sort;
import java.util.ArrayList;
import java.util.List;
import org.deadmaze.database.collections.*;
import org.deadmaze.database.embeds.*;

public final class DBUtils {
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

    /**
     * Fetches all reports.
     * @return A list of reports.
     */
    public static List<Report> findAllReports() {
        return DBManager.getDataStore().find(Report.class).stream().toList();
    }

    /**
     * Searches for cafe posts made by given player.
     * @param playerName The player's name.
     * @return List of cafe posts.
     */
    public static List<CafePost> findCafePostsByPlayerName(String playerName) {
        List<CafePost> matchingPosts = new ArrayList<>();
        List<CafeTopic> topics = DBManager.getDataStore()
                .find(CafeTopic.class)
                .filter(elemMatch("posts", eq("author", playerName)))
                .iterator()
                .toList();

        for (CafeTopic topic : topics) {
            for (CafePost post : topic.getPosts()) {
                if (playerName.equals(post.getAuthor())) {
                    matchingPosts.add(post);
                }
            }
        }

        return matchingPosts;
    }

    /**
     * Searches for cafe posts by topic id.
     * @param topicId The given id.
     * @return The cafe posts if exist or else null.
     */
    public static CafeTopic findCafeTopicById(Long topicId) {
        return DBManager.getDataStore().find(CafeTopic.class).filter(eq("_id", topicId)).first();
    }

    /**
     * Searches for cafe topic by post id.
     * @param postId The given id.
     * @return The cafe topic if exist or else null.
     */
    public static CafeTopic findCafeTopicByPostId(Long postId) {
        return DBManager.getDataStore().find(CafeTopic.class).filter(elemMatch("posts", eq("_id", postId))).first();
    }

    /**
     * Searches for all cafe topics by given community.
     * @param community The given player's community.
     * @return The cafe topics if they exist.
     */
    public static List<CafeTopic> findCafeTopicsByCommunity(String community) {
        return DBManager.getDataStore().find(CafeTopic.class).filter(eq("community", community)).stream().toList();
    }

    /**
     * Fetches all connection logs off given player.
     * @param playerName The given playerName or IP Address.
     * @param isUsingIPAddress Is using ip address instead of nickname.
     * @return Connection logs
     */
    public static List<Loginlog> findConnectionLogs(String playerName, boolean isUsingIPAddress) {
        if(isUsingIPAddress) {
            return DBManager.getDataStore().find(Loginlog.class).filter(eq("ipAddress", playerName)).stream().limit(200).toList();
        }
        return DBManager.getDataStore().find(Loginlog.class).filter(eq("playerName", playerName)).stream().limit(200).toList();
    }

    /**
     * Searches for last active sanction by given name.
     * @param playerName The given player name.
     * @param punishType The given sanction type.
     * @return A sanction object if exist or else null.
     */
    public static Sanction findLatestSanction(String playerName, String punishType) {
        return DBManager.getDataStore().find(Sanction.class).filter(eq("playerName", playerName), eq("type", punishType), eq("state", "Active")).iterator(new FindOptions().sort(Sort.ascending("createdDate"))).tryNext();
    }

    /**
     * Fetches all sanctions of given player.
     * @param playerName The player name.
     * @return A list of sanction object.
     */
    public static List<Sanction> findSanctionsByAccount(String playerName) {
        return DBManager.getDataStore().find(Sanction.class).filter(eq("playerName", playerName)).stream().toList();
    }

    /**
     * Searches for tribe by given name.
     * @param tribeName The tribe name.
     * @return A tribe object.
     */
    public static Tribe findTribeByName(String tribeName) {
        return DBManager.getDataStore().find(Tribe.class).filter(eq("tribeName", tribeName)).first();
    }
}