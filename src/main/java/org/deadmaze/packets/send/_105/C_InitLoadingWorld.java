package org.deadmaze.packets.send._105;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_InitLoadingWorld implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_InitLoadingWorld(int posX, int posY) {
        this.byteArray.writeUnsignedShort(posX);
        this.byteArray.writeUnsignedShort(posY);
    }

    @Override
    public int getC() {
        return 105;
    }

    @Override
    public int getCC() {
        return 36;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}