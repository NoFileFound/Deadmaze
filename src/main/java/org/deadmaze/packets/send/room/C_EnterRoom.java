package org.deadmaze.packets.send.room;

// Imports
import static org.deadmaze.utils.Utils.getCommunityFromLanguage;
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_EnterRoom implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_EnterRoom(String roomName) {
        this.byteArray.writeBoolean(false);
        this.byteArray.writeString(roomName);
        this.byteArray.writeString(roomName.startsWith("*") ? "int" : getCommunityFromLanguage(roomName.substring(0, roomName.indexOf("-"))));
    }

    @Override
    public int getC() {
        return 5;
    }

    @Override
    public int getCC() {
        return 21;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}