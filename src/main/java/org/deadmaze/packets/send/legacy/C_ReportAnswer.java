package org.deadmaze.packets.send.legacy;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_ReportAnswer implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_ReportAnswer(String playerName) {
        this.byteArray.writeString(playerName, false);
    }

    @Override
    public int getC() {
        return 26;
    }

    @Override
    public int getCC() {
        return 9;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}