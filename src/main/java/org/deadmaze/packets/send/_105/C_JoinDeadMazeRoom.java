package org.deadmaze.packets.send._105;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_JoinDeadMazeRoom implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_JoinDeadMazeRoom(String roomName) {
        this.byteArray.writeString(roomName);
    }

    @Override
    public int getC() {
        return 105;
    }

    @Override
    public int getCC() {
        return 3;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}