package org.deadmaze.packets.send.informations;

// Imports
import static org.deadmaze.utils.Utils.getUnixTime;
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_ShopTimestamp implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_ShopTimestamp() {
        this.byteArray.writeInt(getUnixTime());
    }

    @Override
    public int getC() {
        return 28;
    }

    @Override
    public int getCC() {
        return 2;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}