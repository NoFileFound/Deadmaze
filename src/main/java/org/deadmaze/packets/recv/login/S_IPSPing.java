package org.deadmaze.packets.recv.login;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.packets.RecvPacket;

// Packets
import org.deadmaze.packets.send.login.C_IPSPing;

@SuppressWarnings("unused")
public final class S_IPSPing implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        client.sendPacket(new C_IPSPing());
    }

    @Override
    public int getC() {
        return 26;
    }

    @Override
    public int getCC() {
        return 25;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}