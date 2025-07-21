package org.deadmaze.packets.send._105;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_RenderWorldMap implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_RenderWorldMap(int width, int height, byte[] compressedMap) {
        this.byteArray.writeUnsignedShort(width);
        this.byteArray.writeUnsignedShort(height);
        this.byteArray.writeBoolean(compressedMap.length == 0);
        this.byteArray.writeBytes(compressedMap);
    }

    @Override
    public int getC() {
        return 105;
    }

    @Override
    public int getCC() {
        return 37;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}