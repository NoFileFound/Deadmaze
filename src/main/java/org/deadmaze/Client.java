package org.deadmaze;

// Imports
import com.maxmind.geoip2.record.Country;
import java.net.InetSocketAddress;
import lombok.Getter;
import org.bytearray.ByteArray;
import org.deadmaze.database.collections.Account;
import org.deadmaze.libraries.GeoIP;
import org.deadmaze.libraries.Timer;
import org.deadmaze.packets.SendPacket;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

public class Client {
    public int verCode;
    public int loginAttempts;
    public boolean hasSent2FAEmail = false;
    public String osLanguage;
    public String osName;
    public String playerCommunity;
    public String playerToken2FA = "";
    public String playerType;
    public String registerCaptcha;
    private boolean isClosed;
    private final Channel channel;
    @Getter private String ipAddress;
    @Getter final private Server server;
    @Getter final private String countryLangue;
    @Getter final private String countryName;

    // Timers
    public Timer keepAliveTimer;

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

        // Timers
        this.keepAliveTimer = new Timer(Application.getPropertiesInfo().timers.keep_alive.enable, Application.getPropertiesInfo().timers.keep_alive.delay);
        if(!this.server.createAccountTimer.containsKey(this.ipAddress)) {
            this.server.createAccountTimer.put(this.ipAddress, new Timer(Application.getPropertiesInfo().timers.create_account.enable, Application.getPropertiesInfo().timers.create_account.delay));
        }
    }

    public void sendLogin(Account instance, String nickname) {
        //// TODO: Implement
        System.out.println("Client logged");
    }

    /**
     * Closes the connection of current instance.
     */
    public void closeConnection() {
        this.isClosed = true;

        // Cancel all player timers.
        for (Timer timer : new Timer[] {this.keepAliveTimer}) {
            if (timer != null) {
                timer.cancel();
            }
        }

        this.channel.close();
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
}