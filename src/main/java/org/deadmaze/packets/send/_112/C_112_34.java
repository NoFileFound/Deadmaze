package org.deadmaze.packets.send._112;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

/// TODO: Investigate the packet name and arguments. [112, 34]
public final class C_112_34 implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_112_34(boolean arg1, int arg2) {
        this.byteArray.writeBoolean(arg1);
        this.byteArray.writeUnsignedByte(arg2);
    }

    @Override
    public int getC() {
        return 112;
    }

    @Override
    public int getCC() {
        return 34;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}