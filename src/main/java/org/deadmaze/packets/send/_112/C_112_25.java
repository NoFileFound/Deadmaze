package org.deadmaze.packets.send._112;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

/// TODO: Investigate the packet name and arguments. [112, 25]
public final class C_112_25 implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_112_25(int arg1, int arg2, int arg3) {
        this.byteArray.writeInt(arg1);
        this.byteArray.writeInt(arg2);
        this.byteArray.writeInt(arg3);
    }

    @Override
    public int getC() {
        return 112;
    }

    @Override
    public int getCC() {
        return 25;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}