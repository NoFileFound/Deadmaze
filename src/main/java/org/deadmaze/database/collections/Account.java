package org.deadmaze.database.collections;

// Imports
import static org.deadmaze.utils.Utils.getUnixTime;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.deadmaze.database.DBManager;
import org.deadmaze.database.embeds.TribeRank;

@Entity(value = "accounts", useDiscriminator = false)
@Getter
public final class Account {
    private final @Id long id;
    private final String playerName;
    @Setter private String emailAddress;
    @Setter private String password;
    @Setter private long avatarId;
    @Setter private String lastIPAddress;
    private final ArrayList<String> staffRoles;
    @Setter private Long playedTime;
    private final Boolean hasPublicAuthorization;
    @Setter private short cafeBadReputation;
    private final Long regDate;
    @Setter private Integer lastOn;
    @Setter private String soulmate;
    @Setter private Byte playerGender;
    private final List<String> friendList;
    private final List<String> ignoredList;
    @Setter private String tribeName;
    @Setter private TribeRank tribeRank;
    @Setter private int reputationPoints;

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
        this.avatarId = 0;
        this.lastIPAddress = ipAddress;
        this.staffRoles = new ArrayList<>();
        this.playedTime = 0L;
        this.hasPublicAuthorization = false;
        this.cafeBadReputation = 0;
        this.regDate = getUnixTime();
        this.lastOn = 0;
        this.soulmate = "";
        this.playerGender = 0;
        this.friendList = new ArrayList<>();
        this.ignoredList = new ArrayList<>();
        this.tribeName = "";
        this.tribeRank = new TribeRank();
        this.reputationPoints = 0;
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