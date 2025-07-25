package org.deadmaze.packets.send._112;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_PlayerEmoji implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_PlayerEmoji(int sessionId, int emojiId) {
        this.byteArray.writeInt(sessionId);
        this.byteArray.writeUnsignedByte(emojiId);
    }

    @Override
    public int getC() {
        return 112;
    }

    @Override
    public int getCC() {
        return 23;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}