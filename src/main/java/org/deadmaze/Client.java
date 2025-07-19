package org.deadmaze;

// Imports
import com.maxmind.geoip2.record.Country;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import lombok.Getter;
import org.bytearray.ByteArray;
import org.deadmaze.database.collections.Account;
import org.deadmaze.database.collections.Tribe;
import org.deadmaze.libraries.GeoIP;
import org.deadmaze.libraries.Timer;
import org.deadmaze.modules.*;
import org.deadmaze.packets.SendPacket;
import org.deadmaze.packets.TribullePacket;
import org.deadmaze.utils.Utils;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

// Packets
import org.deadmaze.packets.send.login.C_PlayerIdentity;
import org.deadmaze.packets.send.informations.C_ShopTimestamp;
import org.deadmaze.packets.send.tribulle.C_RejoindreCanalPublique;
import org.deadmaze.packets.send.tribulle.C_SwitchNewTribulle;

public class Client {
    public byte silenceType;
    public int verCode;
    public int loginAttempts;
    public boolean isOpenCafe;
    public boolean isOpenFriendList;
    public boolean isOpenTribe;
    public boolean hasSent2FAEmail = false;
    public String currentMarriageInvite;
    public String currentTribeInvite;
    public String osLanguage;
    public String osName;
    public String playerCommunity;
    public String playerToken2FA = "";
    public String playerType;
    public String registerCaptcha;
    public String silenceMessage;
    private boolean isClosed;
    private long loginTime;
    private final Channel channel;
    @Getter private Account account;
    @Getter private int sessionId;
    @Getter private boolean isGuest;
    @Getter private String ipAddress;
    @Getter private String playerName;
    @Getter private String roomName;
    @Getter final private Server server;
    @Getter final private String countryLangue;
    @Getter final private String countryName;

    // Modules
    @Getter private final ParseCafe parseCafeInstance;
    @Getter private final ParseTribulle parseTribulleInstance;

    // Timers
    public Timer keepAliveTimer;
    public Timer reloadCafeTimer;
    public Timer marriageTimer;
    public Timer chatMessageTimer;

    /**
     * Creates a new player in the server.
     * @param server The server.
     * @param channel The channel where player is connected.
     */
    public Client(Server server, Channel channel) {
        Country country = GeoIP.getCountry(this.ipAddress);

        this.server = server;
        this.channel = channel;
        this.ipAddress = ((InetSocketAddress) channel.getRemoteAddress()).getAddress().getHostAddress();
        this.verCode = -1;
        this.loginAttempts = 0;
        this.isClosed = false;
        this.countryLangue = (country != null) ? country.getIsoCode() : "en";
        this.countryName = (country != null) ? country.getName() : "null (proxy)";

        // Modules
        this.parseCafeInstance = new ParseCafe(this);
        this.parseTribulleInstance = new ParseTribulle(this);

        // Timers
        this.keepAliveTimer = new Timer(Application.getPropertiesInfo().timers.keep_alive.enable, Application.getPropertiesInfo().timers.keep_alive.delay);
        this.reloadCafeTimer = new Timer(Application.getPropertiesInfo().timers.reload_cafe.enable, Application.getPropertiesInfo().timers.reload_cafe.delay);
        this.marriageTimer = new Timer(Application.getPropertiesInfo().timers.marriage.enable, 1);
        this.chatMessageTimer = new Timer(Application.getPropertiesInfo().timers.chat_message.enable, Application.getPropertiesInfo().timers.chat_message.delay);
        if(!this.server.createAccountTimer.containsKey(this.ipAddress)) {
            this.server.createAccountTimer.put(this.ipAddress, new Timer(Application.getPropertiesInfo().timers.create_account.enable, Application.getPropertiesInfo().timers.create_account.delay));
        }
    }

    /**
     * Gets the play time.
     * @return The play time in seconds.
     */
    public long getLoginTime() {
        return (Utils.getUnixTime() - this.loginTime);
    }

    /**
     * Sends the required packets on login.
     * @param account The account (null for guests).
     * @param playerName The player name.
     * @param isNewRegistered Is new account.
     */
    public void sendLogin(Account account, String playerName, boolean isNewRegistered) {
        this.account = account;
        this.playerName = playerName;
        this.loginAttempts = 0;
        this.isGuest = false;
        this.hasSent2FAEmail = false;
        this.playerToken2FA = "";
        this.loginTime = Utils.getUnixTime();
        this.sessionId = ++this.server.lastClientSessionId;
        this.parseCafeInstance.initCafeProperties();
        this.server.getPlayers().put(this.playerName, this);

        /// TODO: [112, 34] -> b'\x01\x01'
        this.sendPacket(new C_PlayerIdentity(this));
        this.sendPacket(new C_SwitchNewTribulle(true));
        this.sendPacket(new C_RejoindreCanalPublique("dm-" + this.countryLangue));
        this.sendPacket(new C_ShopTimestamp());
        if(!this.isGuest) {
            this.parseTribulleInstance.sendIdentificationService();

            // tribe
            if(!this.account.getTribeName().isEmpty()) {
                Tribe myTribe = this.server.getTribeByName(this.account.getTribeName());
                for(String member : myTribe.getTribeMembers()) {
                    if(this.server.checkIsConnected(member)) {
                        this.server.getPlayers().get(member).getParseTribulleInstance().sendTribeMemberModification(this.playerName, 1, this.server.getPlayers().get(member).isOpenTribe);
                    }
                }
            }

            if(!this.server.createCafeTopicTimer.containsKey(this.playerName)) {
                this.server.createCafeTopicTimer.put(this.playerName, new Timer(Application.getPropertiesInfo().timers.create_cafe_topic.enable, Application.getPropertiesInfo().timers.create_cafe_topic.delay));
            }

            if(!this.server.createCafePostTimer.containsKey(this.playerName)) {
                this.server.createCafePostTimer.put(this.playerName, new Timer(Application.getPropertiesInfo().timers.create_cafe_post.enable, Application.getPropertiesInfo().timers.create_cafe_post.delay));
            }
        }

        ///this.sendPacket(new C_InitializeProfileCreation());
    }

    /**
     * Calculates all privileges of current player based on privilege level and privilege roles.
     * @return The all privileges that current player has.
     */
    public ArrayList<Integer> calculatePrivileges() {
        if(this.isGuest) {
            return new ArrayList<>();
        }

        ArrayList<Integer> privileges = new ArrayList<>();
        if(this.hasStaffPermission("Sentinelle", "")) {
            privileges.add(7);
        }

        if(this.hasStaffPermission("FunCorp", "")) {
            privileges.add(13);
        }

        if(this.hasStaffPermission("LuaCrew", "")) {
            privileges.add(12);
        }

        if(this.hasStaffPermission("FashionSquad", "")) {
            privileges.add(15);
        }

        if(this.hasStaffPermission("MapCrew", "")) {
            privileges.add(11);
        }

        if(this.hasStaffPermission("Arbitre", "")) {
            privileges.add(3);
        }

        if(this.hasStaffPermission("Modo", "") || this.hasStaffPermission("TrialModo", "")) {
            privileges.add(3);
            privileges.add(5);
        }

        if(this.hasStaffPermission("Admin", "")) {
            privileges.add(10);
        }

        return new ArrayList<>(new HashSet<>(privileges));
    }

    /**
     * Closes the connection of current instance.
     */
    public void closeConnection() {
        this.isClosed = true;
        this.currentMarriageInvite = "";
        this.currentTribeInvite = "";

        // Cancel all player timers.
        for (Timer timer : new Timer[] {this.keepAliveTimer, this.reloadCafeTimer, this.marriageTimer, this.chatMessageTimer}) {
            if (timer != null) {
                timer.cancel();
            }
        }

        if(this.playerName != null) {
            this.server.getPlayers().remove(this.playerName);
            if(!this.isGuest) {
                this.saveDatabase();

                // send disconnect message to all members in the tribe
                if(!this.account.getTribeName().isEmpty()) {
                    Tribe myTribe = this.server.getTribeByName(this.account.getTribeName());
                    for(String member : myTribe.getTribeMembers()) {
                        if(this.server.checkIsConnected(member)) {
                            this.server.getPlayers().get(member).getParseTribulleInstance().sendTribeMemberModification(this.playerName, 0, this.server.getPlayers().get(member).isOpenTribe);
                        }
                    }
                }
            }
        }

        this.channel.close();
    }

    /**
     * Checks if current client has permission.
     * @param position The staff position.
     * @param permissionType The permission.
     * @return If the client has permission.
     */
    public boolean hasStaffPermission(String position, String permissionType) {
        if(this.isGuest) return false;

        if(this.account.getStaffRoles().contains("Admin")) return true;
        if(this.account.getHasPublicAuthorization() && permissionType.equals("StaffChannel")) return true;

        return switch (position) {
            case "Sentinelle" -> this.account.getStaffRoles().contains("Sentinelle");
            case "FunCorp" -> this.account.getStaffRoles().contains("FunCorp");
            case "LuaDev" -> this.account.getStaffRoles().contains("LuaDev");
            case "FashionSquad" -> this.account.getStaffRoles().contains("FashionSquad");
            case "MapCrew" -> this.account.getStaffRoles().contains("MapCrew");
            case "Arbitre" -> this.account.getStaffRoles().contains("Arbitre");
            case "TrialModo" -> this.account.getStaffRoles().contains("TrialModo");
            case "Modo" -> this.account.getStaffRoles().contains("PrivateModo") || this.account.getStaffRoles().contains("PublicModo");
            default -> false;
        };
    }

    /**
     * Broadcast a packet in current player.
     * @param packet The given packet.
     */
    public void sendPacket(SendPacket packet) {
        if(this.isClosed) {
            throw new RuntimeException(Application.getTranslationManager().get("packeterror2"));
        }

        byte[] data = packet.getPacket();
        ByteArray _packet = new ByteArray();

        int length;
        for(length = data.length + 2; length >= 128; length >>= 7) {
            _packet.writeUnsignedByte(((length & 127) | 128));
        }
        _packet.writeUnsignedByte(length);
        _packet.writeUnsignedByte(packet.getC());
        _packet.writeUnsignedByte(packet.getCC());
        _packet.writeBytes(data);

        Application.getLogger().debug(Application.getTranslationManager().get("sendpacket", this.ipAddress, packet.getC(), packet.getCC()));
        this.channel.write(ChannelBuffers.wrappedBuffer(_packet.toByteArray()));
    }

    /**
     * Broadcast a tribulle packet in current player.
     * @param packet The given packet.
     * @param isLegacy Is using the legacy tribulle.
     */
    public void sendTribullePacket(TribullePacket packet, boolean isLegacy) {
        if(this.isClosed) {
            throw new RuntimeException(Application.getTranslationManager().get("packeterror2"));
        }

        byte[] data = packet.getPacket();
        ByteArray _packet = new ByteArray();

        int length;
        for(length = data.length + 4; length >= 128; length >>= 7) {
            _packet.writeUnsignedByte(((length & 127) | 128));
        }
        _packet.writeUnsignedByte(length);
        _packet.writeUnsignedByte(60);
        _packet.writeUnsignedByte((isLegacy ? 1 : 3));
        _packet.writeShort(packet.getTribulleCode());
        _packet.writeBytes(data);

        Application.getLogger().debug(Application.getTranslationManager().get("sendtribullepacket", this.ipAddress, packet.getTribulleCode()));
        this.channel.write(ChannelBuffers.wrappedBuffer(_packet.toByteArray()));
    }

    /**
     * Saves the client's database.
     */
    public void saveDatabase() {
        if(!this.isGuest) {
            this.account.setLastIPAddress(this.ipAddress);
            this.account.setLastOn(Utils.getTribulleTime());
            this.account.setPlayedTime(this.account.getPlayedTime() + this.getLoginTime());
            this.account.save();

            this.server.getCachedAccounts().remove(this.playerName);
        }
    }
}