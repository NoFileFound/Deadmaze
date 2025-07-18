package org.deadmaze.packets.send.informations;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.packets.SendPacket;

public final class C_SetAllowEmailAddress implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_SetAllowEmailAddress() {
        this.byteArray.writeBoolean(Application.getPropertiesInfo().allow_email);
    }

    @Override
    public int getC() {
        return 28;
    }

    @Override
    public int getCC() {
        return 62;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}