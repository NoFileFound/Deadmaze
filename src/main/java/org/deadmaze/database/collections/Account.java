package org.deadmaze.database.collections;

// Imports
import static org.deadmaze.utils.Utils.getUnixTime;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import org.deadmaze.database.DBManager;

@Entity(value = "accounts", useDiscriminator = false)
@Getter
public final class Account {
    private final Long regDate;
    private final @Id long id;
    private final String playerName;
    @Setter private String emailAddress;
    @Setter private String password;
    @Setter private String lastIPAddress;
    private final ArrayList<String> staffRoles;

    /**
     * Creates a new player.
     * @param playerName Nickname.
     * @param emailAddress Email address.
     * @param password Password.
     */
    public Account(final String playerName, final String emailAddress, final String password, final String ipAddress) {
        this.id = DBManager.getCounterValue("lastPlayerId");
        this.playerName = playerName;
        this.emailAddress = emailAddress;
        this.password = password;
        this.regDate = getUnixTime();
        this.lastIPAddress = ipAddress;
        this.staffRoles = new ArrayList<>();
    }

    /**
     * Deletes a player from database.
     */
    public void delete() {
        DBManager.deleteInstance(this);
    }

    /**
     * Updates the database of player.
     */
    public void save() {
        DBManager.saveInstance(this);
    }
}