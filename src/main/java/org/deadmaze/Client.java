package org.deadmaze;

// Imports
import com.maxmind.geoip2.record.Country;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.bytearray.ByteArray;
import org.deadmaze.database.collections.Account;
import org.deadmaze.database.collections.Sanction;
import org.deadmaze.database.collections.Tribe;
import org.deadmaze.enums.SceneLoading;
import org.deadmaze.libraries.GeoIP;
import org.deadmaze.libraries.Pair;
import org.deadmaze.libraries.SrcRandom;
import org.deadmaze.libraries.Timer;
import org.deadmaze.modules.*;
import org.deadmaze.packets.SendPacket;
import org.deadmaze.packets.TribullePacket;
import org.deadmaze.utils.Utils;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

// Packets
import org.deadmaze.packets.send._105.C_105_14_54;
import org.deadmaze.packets.send._105.C_AddPlayerToWorld;
import org.deadmaze.packets.send._105.C_InitLoadingScreenScene;
import org.deadmaze.packets.send._105.C_InitializeProfileCreation;
import org.deadmaze.packets.send._105.C_JoinDeadMazeRoom;
import org.deadmaze.packets.send._112.C_PlayerAddPoses;
import org.deadmaze.packets.send._116.C_SetSecretPassageTime;
import org.deadmaze.packets.send.chat.C_ServerMessage;
import org.deadmaze.packets.send.legacy.C_BanMessageLogin;
import org.deadmaze.packets.send.login.C_PlayerIdentity;
import org.deadmaze.packets.send.informations.C_ShopTimestamp;
import org.deadmaze.packets.send.informations.C_VerifiedEmailAddress;
import org.deadmaze.packets.send.room.info.C_RoomServer;
import org.deadmaze.packets.send.room.info.C_RoomType;
import org.deadmaze.packets.send.room.C_EnterRoom;
import org.deadmaze.packets.send.tribulle.C_RejoindreCanalPublique;
import org.deadmaze.packets.send.tribulle.C_SwitchNewTribulle;

public final class Client {
    public Client lastWatchedClient;
    public SceneLoading sceneLoadingInfo;
    public Pair<Integer, Integer> screenResolution;
    public Pair<Integer, Integer> playerPosition = new Pair<>(0, 0);
    public byte silenceType;
    public int verCode;
    public int loginAttempts;
    public boolean isHidden = false;
    public boolean isMumuted;
    public boolean isSubscribedModoNotifications;
    public boolean isOpenCafe;
    public boolean isOpenFriendList;
    public boolean isOpenModopwet;
    public boolean isOpenTribe;
    public boolean hasSent2FAEmail = false;
    public String currentMarriageInvite = "";
    public String currentMessage = "";
    public String currentTribeInvite = "";
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
    @Getter private String playerName;
    @Getter private String roomName;
    @Getter private String lastRoomName;
    @Getter final private Server server;
    @Getter final private String countryLangue;
    @Getter final private String countryName;
    @Getter final private String ipAddress;
    @Getter final private List<String> groupPlayers;
    @Getter private final List<String> modopwetChatNotificationCommunities;
    @Getter private final Map<String, Integer> modoCommunitiesCount;
    @Getter private final ArrayList<Client> currentWatchers;
    @Getter @Setter private Room room;

    // Modules
    @Getter private final ParseAbilities parseAbilitiesInstance;
    @Getter private final ParseCafe parseCafeInstance;
    @Getter private final ParseTribulle parseTribulleInstance;
    @Getter private final ParseModopwet parseModopwetInstance;

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
        Country country = GeoIP.getCountry(((InetSocketAddress) channel.getRemoteAddress()).getAddress().getHostAddress());

        this.server = server;
        this.channel = channel;
        this.ipAddress = ((InetSocketAddress) channel.getRemoteAddress()).getAddress().getHostAddress();
        this.verCode = -1;
        this.loginAttempts = 0;
        this.isClosed = false;
        this.countryLangue = (country != null) ? country.getIsoCode() : "en";
        this.countryName = (country != null) ? country.getName() : "null (proxy)";
        this.groupPlayers = new ArrayList<>();
        this.modopwetChatNotificationCommunities = new ArrayList<>();
        this.modoCommunitiesCount = new HashMap<>();
        this.currentWatchers = new ArrayList<>();

        // Modules
        this.parseAbilitiesInstance = new ParseAbilities(this);
        this.parseCafeInstance = new ParseCafe(this);
        this.parseTribulleInstance = new ParseTribulle(this);
        this.parseModopwetInstance = new ParseModopwet(this);

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
            this.server.getWhisperMessages().remove(this.playerName);

            // remove him from the room
            if (this.room != null) {
                this.room.removePlayer(this);
                this.room = null;
            }

            // Modopwet watchers
            if(this.server.getGameReports().containsKey(this.playerName) && !this.server.getGameReports().get(this.playerName).getIsDeleted()) {
                for(Client player : this.server.getPlayers().values()) {
                    if(player.isSubscribedModoNotifications && player.getModopwetChatNotificationCommunities().contains(this.playerCommunity)) {
                        player.sendPacket(new C_ServerMessage(true, String.format("<ROSE>[Modopwet] [%s]</ROSE> <BV>%s</BV> has been disconnected from the game.", this.playerCommunity, this.playerName)));
                    } else if(player.isOpenModopwet) {
                        player.getParseModopwetInstance().sendOpenModopwet(true);
                    }
                }
            }

            if(!this.currentWatchers.isEmpty()) {
                for(Client watcher : this.currentWatchers) {
                    watcher.lastWatchedClient = null;
                    watcher.parseModopwetInstance.sendWatchPlayer("");
                    watcher.isHidden = false;
                    watcher.sendEnterRoom(this.server.getRecommendedRoom(""));

                }
                this.currentWatchers.clear();
            }

            if(this.lastWatchedClient != null) {
                this.lastWatchedClient.currentWatchers.remove(this);
                this.lastWatchedClient = null;
            }

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
     * Enters a given room.
     * @param roomName The room name.
     */
    public void sendEnterRoom(String roomName) {
        if(this.lastWatchedClient != null && !roomName.equals(this.lastWatchedClient.getRoomName())) {
            return;
        }

        roomName = roomName.replace("<", "lt;");
        if(!roomName.startsWith("*")) {
            if(!(roomName.length() > 3 && roomName.charAt(2) == '-')) {
                roomName = this.playerCommunity.toLowerCase() + "-" + roomName;
            } else if(this.hasStaffPermission("MapCrew", "JoinCommunityRooms")) {
                roomName = this.playerCommunity.toLowerCase() + roomName.substring(2);
            }
        }

        if (this.room != null) {
            this.lastRoomName = this.room.getRoomName();
            this.room.removePlayer(this);
        }

        this.roomName = roomName;
        this.sendPacket(new C_RoomServer(0));
        this.sendPacket(new C_RoomType(roomName.contains("pretuto_") ? 61 : 60));
        this.sendPacket(new C_JoinDeadMazeRoom(this.roomName));
        this.sendPacket(new C_EnterRoom(this.roomName));
        this.server.addClientToRoom(this, this.roomName);
        if(!this.isGuest) {
            // Notify friends when you change the room.
            for(String friendName : this.account.getFriendList()) {
                Client playerObj = this.server.getPlayers().get(friendName);
                if(playerObj != null && playerObj.getAccount().getFriendList().contains(this.playerName) && playerObj.isOpenFriendList) {
                    playerObj.getParseTribulleInstance().sendFriendModification(this.playerName, 1);
                }
            }

            // notify all tribe members when you change the room.
            if(!this.account.getTribeName().isEmpty()) {
                Tribe myTribe = this.server.getTribeByName(this.account.getTribeName());
                for(String member : myTribe.getTribeMembers()) {
                    if(this.server.checkIsConnected(member) && !member.equals(this.playerName)) {
                        this.server.getPlayers().get(member).getParseTribulleInstance().sendTribeMemberModification(this.playerName, -1, this.server.getPlayers().get(member).isOpenTribe);
                    }
                }
            }
        }

        if(this.server.getGameReports().containsKey(this.playerName)) {
            if(!this.currentWatchers.isEmpty()) {
                for(Client watcher : this.currentWatchers) {
                    watcher.sendEnterRoom(this.getRoom().getRoomName());
                }
            } else if(this.server.getGameReports().containsKey(this.playerName) && !this.server.getGameReports().get(this.playerName).getIsDeleted() && this.lastRoomName != null) {
                for(Client player : this.server.getPlayers().values()) {
                    if(player.isSubscribedModoNotifications && player.getModopwetChatNotificationCommunities().contains(this.playerCommunity) && !player.getCurrentWatchers().contains(this.lastWatchedClient)) {
                        player.sendPacket(new C_ServerMessage(true, String.format("<ROSE>[Modopwet]</ROSE> The player <BV>%s</BV> left the room <N>[%s]</N> and went to the room <N>[%s]</N>. %s", this.playerName, this.lastRoomName, this.getRoom().getRoomName(), this.room.getRoomName().equals(player.getRoom().getRoomName()) ? "" : String.format(" (<CEP><a href='event:join;%s'>Watch</a></CEP> - <CEP><a href='event:follow;%s'>Follow</a></CEP>)", this.playerName, this.playerName))));
                    }
                }
            }
        }

        if(this.lastWatchedClient != null && this.lastWatchedClient.lastRoomName != null) {
            this.sendPacket(new C_ServerMessage(true, String.format("<ROSE>[Modopwet]</ROSE> The player <BV>%s</BV> left the room <N>[%s]</N> and went to the room <N>[%s]</N>. %s", this.lastWatchedClient.playerName, this.lastWatchedClient.lastRoomName, this.lastWatchedClient.getRoom().getRoomName(), "(<CEP><a href='event:stopfollow'>Stop following</a></CEP>)")));
        }
    }

    /**
     * Sends the scene loading on the player.
     */
    public void sendLoadScene() {
        if(this.sceneLoadingInfo == SceneLoading.LOADING) {
            return;
        }

        this.sceneLoadingInfo = SceneLoading.INIT;
        this.sendPacket(new C_105_14_54(SrcRandom.RandomNumber(1511, 4578)));
        this.sendPacket(new C_InitLoadingScreenScene(50, 50));
        if(this.account.getPlayerLook().isEmpty()) {
            this.sendPacket(new C_InitializeProfileCreation());
        }
    }

    /**
     * Sends the removal of scene loading on the player.
     */
    public void sendLoadRemoveScene() {
        if(this.sceneLoadingInfo == SceneLoading.NONE) {
            return;
        }

        this.sceneLoadingInfo = SceneLoading.NONE;
    }

    /**
     * Sends the required packets on login.
     * @param account The account (null for guests).
     * @param playerName The player name.
     * @param isRegistered Is the player's account new.
     */
    public void sendLogin(Account account, String playerName, boolean isRegistered) {
        this.account = account;
        this.playerName = playerName;
        this.loginAttempts = 0;
        this.isGuest = false;
        this.hasSent2FAEmail = false;
        this.playerToken2FA = "";
        this.loginTime = Utils.getUnixTime();
        this.roomName = (!this.account.getPlayerLook().isEmpty()) ? this.server.getRecommendedRoom("village") : this.server.getRecommendedRoom("pretuto_") + " / " + this.playerName;
        if (this.account != null) {
            Sanction mySanction = this.server.getLatestSanction(playerName, "bandef");
            if(mySanction == null) {
                mySanction = this.server.getLatestSanction(playerName, "banjeu");
            }

            if(mySanction != null) {
                if(mySanction.getIsPermanent()) {
                    this.sendOldPacket(new C_BanMessageLogin(mySanction.getReason()));
                } else {
                    long hours = (mySanction.getExpirationDate() - Utils.getUnixTime()) / 3600;

                    this.sendOldPacket(new C_BanMessageLogin(hours * 3600000, mySanction.getReason()));
                }
                return;
            }
        }

        this.sessionId = ++this.server.lastClientSessionId;
        this.parseCafeInstance.initCafeProperties();
        this.server.recordLoginLog(playerName, ipAddress, this.countryName, this.playerCommunity);
        this.server.getPlayers().put(this.playerName, this);
        this.sendPacket(new C_PlayerIdentity(this));
        this.sendPacket(new C_SwitchNewTribulle(true));
        this.sendPacket(new C_RejoindreCanalPublique("dm-" + this.countryLangue));
        this.sendPacket(new C_ShopTimestamp());
        this.sendPacket(new C_SetSecretPassageTime(false, 0, 0));
        this.parseAbilitiesInstance.sendInitAbilities();
        this.sendPacket(new C_PlayerAddPoses(this.account.getPlayerPoses()));
        if(!this.isGuest) {
            this.sendPacket(new C_VerifiedEmailAddress(true));
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

        this.sendEnterRoom(this.roomName);
        if(this.server.getGameReports().containsKey(this.playerName) && !this.server.getGameReports().get(this.playerName).getIsDeleted()) {
            this.server.getGameReports().get(this.playerName).setPlayerCommunity(this.playerCommunity);
            for(Client player : this.server.getPlayers().values()) {
                if(player.isSubscribedModoNotifications && player.getModopwetChatNotificationCommunities().contains(this.playerCommunity)) {
                    player.sendPacket(new C_ServerMessage(true, String.format("<ROSE>[Modopwet] [%s]</ROSE> <BV>%s</BV> has been connected on the game in room [<N>%s</N>] %s", this.playerCommunity, this.playerName, this.room.getRoomName(), this.room.getRoomName().equals(player.getRoom().getRoomName()) ? "" : String.format(" (<CEP><a href='event:join;%s'>Watch</a></CEP> - <CEP><a href='event:follow;%s'>Follow</a></CEP>)", this.playerName, this.playerName))));
                } else if(player.isOpenModopwet) {
                    player.getParseModopwetInstance().sendOpenModopwet(true);
                }
            }
        }
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
     * Broadcast a legacy (legacy) packet in current player.
     * @param packet The given packet.
     */
    public void sendOldPacket(SendPacket packet) {
        if(this.isClosed) {
            throw new RuntimeException(Application.getTranslationManager().get("packeterror2"));
        }

        ByteArray data = new ByteArray();
        ByteArray _packet = new ByteArray();

        data.writeUnsignedShort((packet.getPacket().length > 0 ? packet.getPacket().length + 3 : 2));
        data.writeString(String.valueOf((char) packet.getC()) + (char) packet.getCC(), false);
        data.writeByte(1);
        data.writeBytes(packet.getPacket());

        int length;
        for(length = data.getLength() + 2; length >= 128; length >>= 7) {
            _packet.writeUnsignedByte(((length & 127) | 128));
        }
        _packet.writeUnsignedByte(length);
        _packet.writeUnsignedByte(1);
        _packet.writeUnsignedByte(1);
        _packet.writeBytes(data.toByteArray());

        Application.getLogger().debug(Application.getTranslationManager().get("sendlegacypacket", this.ipAddress, packet.getC(), packet.getCC(), _packet));
        this.channel.write(ChannelBuffers.wrappedBuffer(_packet.toByteArray()));
    }

    /**
     * Send the all players in the room when a new player joins.
     */
    public void sendRoomPlayers() {
        for(Client player : this.room.getPlayers().values()) {
            if(player != this) {
                this.sendPacket(new C_AddPlayerToWorld(player));
            }
        }

        this.sendPacket(new C_AddPlayerToWorld(this));
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