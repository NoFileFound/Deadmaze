package org.deadmaze.packets.recv.newpackets;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_CafeWarnings implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {

    }

    @Override
    public int getC() {
        return 149;
    }

    @Override
    public int getCC() {
        return 5;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}