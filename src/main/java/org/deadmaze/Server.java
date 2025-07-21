package org.deadmaze;

// Imports
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.deadmaze.command.CommandLoader;
import org.deadmaze.connection.ClientHandler;
import org.deadmaze.connection.Decoder;
import org.deadmaze.connection.Encoder;
import org.deadmaze.database.DBUtils;
import org.deadmaze.database.collections.Account;
import org.deadmaze.database.collections.Sanction;
import org.deadmaze.database.collections.Tribe;
import org.deadmaze.libraries.Pair;
import org.deadmaze.libraries.Timer;
import org.deadmaze.packets.PacketHandler;
import org.deadmaze.packets.RecvPacket;
import org.deadmaze.utils.Utils;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

// Packets
import org.deadmaze.packets.send.chat.C_ServerMessage;
import org.deadmaze.packets.send.chat.C_StaffChannelMessage;

public final class Server {
    private boolean isClosed;
    private final Map<Integer, Client> clientSessions;
    private final List<Channel> channels;
    @Getter public int lastClientSessionId;
    @Getter private final Object2ObjectMap<String, Client> players;
    @Getter private final Object2ObjectMap<String, Room> rooms;
    @Getter private final List<String> tempBlackList;
    @Getter private final ArrayList<Integer> blacklistedPackets;
    @Getter private final PacketHandler packetHandler;
    @Getter private final CommandLoader commandHandler;
    @Getter private final Object2ObjectMap<String, List<String>> chats;
    @Getter private final Object2ObjectMap<String, Object2ObjectMap<String, Deque<String[]>>> whisperMessages;
    @Getter private final Object2ObjectMap<String, Object2ObjectMap<String, Deque<String[]>>> chatMessages;

    // Cache
    @Getter private Object2ObjectMap<String, Account> cachedAccounts;
    @Getter private Object2ObjectMap<String, Tribe> cachedTribes;
    @Getter private Object2ObjectMap<Pair<String, String>, Sanction> cachedSanctions;

    // Timers
    public final Map<String, Timer> createAccountTimer;
    public final Map<String, Timer> createCafeTopicTimer;
    public final Map<String, Timer> createCafePostTimer;

    /**
     * Creates a new instance of the server.
     */
    public Server() {
        this.isClosed = false;
        this.clientSessions = new HashMap<>();
        this.channels = new ArrayList<>();
        this.packetHandler = new PacketHandler(RecvPacket.class);
        this.commandHandler = new CommandLoader();
        this.players = new Object2ObjectOpenHashMap<>();
        this.rooms = new Object2ObjectOpenHashMap<>();
        this.tempBlackList = new ArrayList<>();
        this.blacklistedPackets = new ArrayList<>();
        this.chats = new Object2ObjectOpenHashMap<>();
        this.whisperMessages = new Object2ObjectOpenHashMap<>();
        this.chatMessages = new Object2ObjectOpenHashMap<>();

        // Cache
        this.cachedAccounts = new Object2ObjectOpenHashMap<>();
        this.cachedTribes = new Object2ObjectOpenHashMap<>();
        this.cachedSanctions = new Object2ObjectOpenHashMap<>();

        // Timers
        this.createAccountTimer = new HashMap<>();
        this.createCafeTopicTimer = new HashMap<>();
        this.createCafePostTimer = new HashMap<>();
    }

    /**
     * Creates a new client session.
     * @param channel The network channel associated with the client.
     */
    public void addClientSession(final Channel channel) {
        Client client = new Client(this, channel);
        this.clientSessions.put(channel.getId(), client);
        channel.setAttachment(client);
    }

    /**
     * Adds player to the given room.
     *
     * @param player   The player.
     * @param roomName The room name.
     */
    public void addClientToRoom(Client player, String roomName) {
        if (this.rooms.containsKey(roomName)) {
            this.rooms.get(roomName).addPlayer(player);
        } else {
            Room room = new Room(this, roomName, player.getPlayerName());
            this.rooms.put(roomName, room);
            room.addPlayer(player);
        }
    }

    /**
     * Shutdowns the server.
     */
    public void closeServer() {
        for (Timer timer : createCafeTopicTimer.values()) {
            if (timer != null) {
                timer.cancel();
            }
        }
        for (Timer timer : createCafePostTimer.values()) {
            if (timer != null) {
                timer.cancel();
            }
        }
        for (Timer timer : createAccountTimer.values()) {
            if (timer != null) {
                timer.cancel();
            }
        }

        for (Client player : this.players.values()) {
            player.saveDatabase();
        }

        for (Channel channel : this.channels) {
            channel.unbind();
        }

        this.isClosed = true;
        System.exit(0);
    }

    /**
     * Gets the number of all players in the game.
     *
     * @return Player count.
     */
    public int getPlayersCount() {
        return this.players.size();
    }

    /**
     * Gets the account object from the given player name.
     *
     * @param playerName The player name.
     * @return An account object.
     */
    public Account getPlayerAccount(String playerName) {
        if (this.players.containsKey(playerName)) {
            if(this.players.get(playerName).isGuest()) return null;
            return this.players.get(playerName).getAccount();
        }

        if (this.cachedAccounts.containsKey(playerName)) {
            return this.cachedAccounts.get(playerName);
        }

        return DBUtils.findAccountByNickname(playerName);
    }

    /**
     * Gets the latest sanction of the given player.
     *
     * @param playerName Player's name.
     * @param sanctionType Punishment type.
     * @return A pair of punishment duration and punishment reason.
     */
    public Sanction getLatestSanction(String playerName, String sanctionType) {
        Pair<String, String> myPair = new Pair<>(playerName, sanctionType);
        if(this.cachedSanctions.get(myPair) != null) {
            Sanction mySanction = this.cachedSanctions.get(myPair);
            if(mySanction.getType().equals(sanctionType) && mySanction.getState().equals("Active")) {
                long time = mySanction.getExpirationDate();
                long currentTime = Utils.getUnixTime();
                if(mySanction.getIsPermanent() || time > currentTime) {
                    return mySanction;
                } else {
                    mySanction.setState("Expired");
                    mySanction.save();
                    return null;
                }
            }
        }

        Sanction mySanction = DBUtils.findLatestSanction(playerName, sanctionType);
        if(mySanction != null) {
            this.cachedSanctions.put(myPair, mySanction);
            return this.getLatestSanction(playerName, sanctionType);
        }

        return null;
    }

    /**
     * Checks if given room has enough players and recommends a new one if there are many players.
     *
     * @param roomName  The player's room name.
     * @return The room that has enough players.
     */
    public String getRecommendedRoom(String roomName) {
        if (roomName.isEmpty()) roomName = "1";

        String baseKey = roomName.replaceAll("\\d+$", "");
        String numberPart = roomName.replaceAll("\\D", "");
        int number = numberPart.isEmpty() ? 1 : Integer.parseInt(numberPart);

        if (numberPart.isEmpty()) {
            roomName = baseKey + "1";
        }

        String result = roomName;
        while (rooms.containsKey(result)) {
            Room room = rooms.get(result);
            if (room.getPlayersCount() >= 50) {
                result = baseKey + (++number);
            } else {
                break;
            }
        }
        return result;
    }

    /**
     * Gets the tribe object by given tribe name.
     *
     * @param tribeName The given tribe name.
     * @return A tribe object.
     */
    public Tribe getTribeByName(String tribeName) {
        if (this.cachedTribes.containsKey(tribeName)) {
            return this.cachedTribes.get(tribeName);
        }

        Tribe myTribe = DBUtils.findTribeByName(tribeName);
        if (myTribe == null) return null;

        this.cachedTribes.put(tribeName, myTribe);
        return myTribe;
    }

    /**
     * Checks if the given player is connected in the game.
     *
     * @param playerName The given player's name.
     * @return True if he is connected or else false.
     */
    public boolean checkIsConnected(String playerName) {
        return this.players.containsKey(playerName);
    }

    /**
     * Sends a message in #Server channel.
     *
     * @param message The message to send.
     * @param isTab   Is sent in general chat instead of #Server.
     * @param other   Send to everyone except the given player.
     */
    public void sendServerMessage(String message, boolean isTab, Client other) {
        for (Client client : this.players.values()) {
            if (client.hasStaffPermission("Arbitre", "ServerMsg")) {
                if (other != null) {
                    if (client != other) {
                        client.sendPacket(new C_ServerMessage(isTab, message));
                    }
                } else {
                    client.sendPacket(new C_ServerMessage(isTab, message));
                }
            }
        }
    }

    /**
     * Sends a message in the staff channel.
     *
     * @param channelId    The staff channel id.
     * @param rawMessage   The message to send.
     * @param isInt        Send the message in every staff player (Exceptions are #Modo and #Arbitre).
     * @param clientName   Sender's nickname.
     * @param clientLangue Sender's community.
     */
    public void sendStaffChannelMessage(int channelId, String rawMessage, boolean isInt, String clientName, String clientLangue) {
        for (Client client : this.players.values()) {
            if (channelId > 1 && channelId < 6 && client.hasStaffPermission("Modo", "StaffChannel") || client.hasStaffPermission("TrialModo", "StaffChannel")) {
                if (isInt) {
                    client.sendPacket(new C_StaffChannelMessage(channelId, clientName, rawMessage));
                } else if (client.playerCommunity.equals(clientLangue)) {
                    client.sendPacket(new C_StaffChannelMessage(channelId, clientName, rawMessage));
                }
            } else if (channelId == 9 && client.hasStaffPermission("FunCorp", "StaffChannel")) {
                client.sendPacket(new C_StaffChannelMessage(channelId, clientName, rawMessage));
            } else if (channelId == 8 && client.hasStaffPermission("MapCrew", "StaffChannel")) {
                client.sendPacket(new C_StaffChannelMessage(channelId, clientName, rawMessage));
            } else if (channelId == 7 && client.hasStaffPermission("LuaDev", "StaffChannel")) {
                client.sendPacket(new C_StaffChannelMessage(channelId, clientName, rawMessage));
            } else if (channelId == 10 && client.hasStaffPermission("FashionSquad", "StaffChannel")) {
                client.sendPacket(new C_StaffChannelMessage(channelId, clientName, rawMessage));
            } else if (channelId == 2 || channelId == 5 && client.hasStaffPermission("Arbitre", "StaffChannel")) {
                client.sendPacket(new C_StaffChannelMessage(channelId, clientName, rawMessage));
            }
        }
    }

    /**
     * Initializes the server.
     */
    public void startServer() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!this.isClosed) {
                this.closeServer();
            }
        }));

        if (!Application.getSwfInfo().ports.isEmpty()) {
            ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
            ChannelPipeline pipeline = bootstrap.getPipeline();
            pipeline.addLast("encoder", new Encoder());
            pipeline.addLast("decoder", new Decoder());
            pipeline.addLast("handler", new ClientHandler(this));

            for (Integer port : Application.getSwfInfo().ports) {
                this.channels.add(bootstrap.bind(new InetSocketAddress(port)));
            }
        } else {
            Application.getLogger().error(Application.getTranslationManager().get("startfailure"));
        }
    }

    /**
     * Removes the client session.
     * @param channel The network channel associated with the client.
     */
    public void removeClientSession(final Channel channel) {
        this.clientSessions.remove(channel.getId());
    }
}