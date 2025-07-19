package org.deadmaze.packets.recv.login;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_ExitTheGame implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        client.closeConnection();
    }

    @Override
    public int getC() {
        return 26;
    }

    @Override
    public int getCC() {
        return 50;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}