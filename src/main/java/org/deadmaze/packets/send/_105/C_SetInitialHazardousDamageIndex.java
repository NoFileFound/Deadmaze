package org.deadmaze.packets.send._105;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_SetInitialHazardousDamageIndex implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_SetInitialHazardousDamageIndex(int idx) {
        this.byteArray.writeUnsignedByte(99);
        this.byteArray.writeUnsignedByte(idx);
    }

    @Override
    public int getC() {
        return 105;
    }

    @Override
    public int getCC() {
        return 13;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}