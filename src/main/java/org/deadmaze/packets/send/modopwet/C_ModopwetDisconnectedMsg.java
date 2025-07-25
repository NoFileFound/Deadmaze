package org.deadmaze.packets.send.modopwet;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_ModopwetDisconnectedMsg implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_ModopwetDisconnectedMsg(String playerName) {
        this.byteArray.writeString(playerName);
    }

    @Override
    public int getC() {
        return 25;
    }

    @Override
    public int getCC() {
        return 6;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}