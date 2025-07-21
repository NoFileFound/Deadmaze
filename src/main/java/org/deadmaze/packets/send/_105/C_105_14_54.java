package org.deadmaze.packets.send._105;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

/// TODO: Investigate the packet name and arguments. [105, 14] -> 54
public final class C_105_14_54 implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_105_14_54(int arg1) {
        this.byteArray.writeUnsignedByte(54);
        this.byteArray.writeUnsignedShort(arg1);
    }

    @Override
    public int getC() {
        return 105;
    }

    @Override
    public int getCC() {
        return 14;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}