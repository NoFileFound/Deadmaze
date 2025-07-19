package org.deadmaze.packets.recv.tribulle;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_NewTribulle implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        if(client.isGuest()) return;

        data.decryptPacket(Application.getSwfInfo().packet_keys, fingerPrint);
        client.getParseTribulleInstance().handleTribullePacket(data.readShort(), data.readInt(), data);
    }

    @Override
    public int getC() {
        return 60;
    }

    @Override
    public int getCC() {
        return 3;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}