package org.deadmaze.packets.recv.cafe;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_OpenCafe implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        client.isOpenCafe = data.readBoolean();

        client.getParseCafeInstance().sendOpenCafe();
    }

    @Override
    public int getC() {
        return 30;
    }

    @Override
    public int getCC() {
        return 45;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}