package org.deadmaze.packets.send._112;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_PlayerPose implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_PlayerPose(int sessionId, int poseId, boolean isLooping) {
        this.byteArray.writeInt(sessionId);
        this.byteArray.writeUnsignedByte(poseId);
        this.byteArray.writeBoolean(isLooping);
        this.byteArray.writeBoolean(isLooping);
    }

    @Override
    public int getC() {
        return 112;
    }

    @Override
    public int getCC() {
        return 30;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}