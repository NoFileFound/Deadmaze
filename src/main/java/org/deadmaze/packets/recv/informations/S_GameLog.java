package org.deadmaze.packets.recv.informations;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_GameLog implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        int C = data.readByte() & 0xFF;
        int CC = data.readByte() & 0xFF;
        int OldC = data.readByte() & 0xFF;
        int OldCC = data.readByte() & 0xFF;
        String error = data.readString();

        if(C == 1 && CC == 1) {
            // Legacy
            Application.getLogger().warn(String.format("[Legacy][%d, %d] %s", OldC, OldCC, error));
        }
        else if(C == 60) {
            // Tribulle
            Application.getLogger().warn(String.format("[Tribulle][%s] %s", (CC == 3 ? "New" : "Legacy"), error));
        }
        else {
            Application.getLogger().warn(String.format("[Packet][%d, %d] %s", C, CC, error));
        }

        if(!Application.getPropertiesInfo().is_debug) client.closeConnection(); // tfm moment
    }

    @Override
    public int getC() {
        return 28;
    }

    @Override
    public int getCC() {
        return 4;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}