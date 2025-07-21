package org.deadmaze.packets.send._105;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_InitLoadingWorld implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    /// TODO: Figure what unk1 and unk2 are for.
    public C_InitLoadingWorld(int unk1, int unk2) {
        this.byteArray.writeUnsignedShort(unk1);
        this.byteArray.writeUnsignedShort(unk2);
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