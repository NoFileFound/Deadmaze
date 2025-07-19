package org.deadmaze.packets.send.newpackets;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_ShowCafeWarnings implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_ShowCafeWarnings(short amount) {
        this.byteArray.writeShort(amount);
    }

    @Override
    public int getC() {
        return 144;
    }

    @Override
    public int getCC() {
        return 11;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}