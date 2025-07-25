package org.deadmaze.packets;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Client;

public interface RecvPacket {
    default int getCode() {
        return (this.getC() << 8) | (this.getCC() & 0xFF);
    }

    void handle(Client client, int fingerPrint, ByteArray data);

    int getC();
    int getCC();
    boolean isLegacyPacket(); // is an old packet
}