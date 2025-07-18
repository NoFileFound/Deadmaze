package org.deadmaze.packets.recv.login;

// Imports
import java.util.concurrent.TimeUnit;
import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_KeepAlive implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        client.keepAliveTimer.schedule(client::closeConnection, TimeUnit.SECONDS);
    }

    @Override
    public int getC() {
        return 26;
    }

    @Override
    public int getCC() {
        return 26;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}