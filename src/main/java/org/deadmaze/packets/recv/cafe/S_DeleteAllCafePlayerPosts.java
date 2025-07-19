package org.deadmaze.packets.recv.cafe;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_DeleteAllCafePlayerPosts implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        client.getParseCafeInstance().sendDeleteAllCafePlayerPosts(data.readInt(), data.readString());
    }

    @Override
    public int getC() {
        return 30;
    }

    @Override
    public int getCC() {
        return 48;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}