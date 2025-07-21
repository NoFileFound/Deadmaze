package org.deadmaze.packets.send._112;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_SetPlayerVitals implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_SetPlayerVitals(int restPoints, int restPointsMax, int thirstPoints, int thirstPointsMax, int foodPoints, int foodPointsMax) {
        this.byteArray.writeUnsignedByte(foodPoints);
        this.byteArray.writeUnsignedByte(foodPointsMax);
        this.byteArray.writeUnsignedByte(thirstPoints);
        this.byteArray.writeUnsignedByte(thirstPointsMax);
        this.byteArray.writeUnsignedByte(restPoints);
        this.byteArray.writeUnsignedByte(restPointsMax);
    }

    @Override
    public int getC() {
        return 112;
    }

    @Override
    public int getCC() {
        return 1;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}