package org.deadmaze;

// Imports
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.deadmaze.connection.ClientHandler;
import org.deadmaze.connection.Decoder;
import org.deadmaze.connection.Encoder;
import org.deadmaze.libraries.Timer;
import org.deadmaze.packets.PacketHandler;
import org.deadmaze.packets.RecvPacket;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class Server {
    private boolean isClosed;
    private final Map<Integer, Client> clientSessions;
    private final List<Channel> channels;
    @Getter private final Object2ObjectMap<String, Client> players;
    @Getter private final List<String> tempBlackList;
    @Getter private final ArrayList<Integer> blacklistedPackets;
    @Getter private final PacketHandler packetHandler;

    // Timers
    public final Map<String, Timer> createAccountTimer;

    /**
     * Creates a new instance of the server.
     */
    public Server() {
        this.isClosed = false;
        this.clientSessions = new HashMap<>();
        this.channels = new ArrayList<>();
        this.packetHandler = new PacketHandler(RecvPacket.class);
        this.players = new Object2ObjectOpenHashMap<>();
        this.tempBlackList = new ArrayList<>();
        this.blacklistedPackets = new ArrayList<>();

        // Timers
        this.createAccountTimer = new HashMap<>();
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
     * Shutdowns the server.
     */
    public void closeServer() {

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
     * Creates a new client session.
     * @param channel The network channel associated with the client.
     */
    public void addClientSession(final Channel channel) {
        Client client = new Client(this, channel);
        this.clientSessions.put(channel.getId(), client);
        channel.setAttachment(client);
    }

    /**
     * Removes the client session.
     * @param channel The network channel associated with the client.
     */
    public void removeClientSession(final Channel channel) {
        this.clientSessions.remove(channel.getId());
    }
}