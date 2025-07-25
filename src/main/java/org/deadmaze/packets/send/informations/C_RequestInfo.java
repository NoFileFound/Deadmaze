package org.deadmaze.packets.send.informations;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_RequestInfo implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_RequestInfo() {
        this.byteArray.writeString("http://51.158.113.197/info.php");
    }

    @Override
    public int getC() {
        return 28;
    }

    @Override
    public int getCC() {
        return 50;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}