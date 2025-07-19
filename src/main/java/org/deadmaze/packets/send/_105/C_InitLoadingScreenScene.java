package org.deadmaze.packets.send._105;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_InitLoadingScreenScene implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_InitLoadingScreenScene(int worldId, int imageId) {
        this.byteArray.writeShort((short)worldId);
        this.byteArray.writeShort((short)imageId);
    }

    @Override
    public int getC() {
        return 105;
    }

    @Override
    public int getCC() {
        return 79;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}