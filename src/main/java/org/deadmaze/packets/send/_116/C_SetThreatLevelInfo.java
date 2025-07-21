package org.deadmaze.packets.send._116;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_SetThreatLevelInfo implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_SetThreatLevelInfo(int currentXp, int xpToNextLevel, int threatLvl) {
        this.byteArray.writeUnsignedInt(currentXp);
        this.byteArray.writeUnsignedShort(xpToNextLevel);
        this.byteArray.writeUnsignedShort(threatLvl);
    }

    @Override
    public int getC() {
        return 116;
    }

    @Override
    public int getCC() {
        return 8;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}