package org.deadmaze.packets.recv.informations;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_SystemInformation implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        client.osLanguage = data.readString();
        client.osName = data.readString();

        data.readString(); // flash version
    }

    @Override
    public int getC() {
        return 28;
    }

    @Override
    public int getCC() {
        return 17;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}