package org.deadmaze.packets.send._116;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_SetSecretPassageTime implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_SetSecretPassageTime(boolean enable, int seconds, int remainingItems) {
        this.byteArray.writeBoolean(enable);
        if(enable) {
            this.byteArray.writeUnsignedShort(seconds);
            this.byteArray.writeUnsignedShort(remainingItems);
        }
    }

    @Override
    public int getC() {
        return 116;
    }

    @Override
    public int getCC() {
        return 17;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}