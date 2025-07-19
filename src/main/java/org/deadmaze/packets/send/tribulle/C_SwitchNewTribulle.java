package org.deadmaze.packets.send.tribulle;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_SwitchNewTribulle implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_SwitchNewTribulle(boolean switchbulle) {
        this.byteArray.writeBoolean(switchbulle);
    }

    @Override
    public int getC() {
        return 60;
    }

    @Override
    public int getCC() {
        return 4;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}