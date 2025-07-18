package org.deadmaze.packets.send.login;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_AccountError implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_AccountError(int errorCode) {
        this.byteArray.writeByte(errorCode);
        this.byteArray.writeString("");
        this.byteArray.writeString("");
    }

    public C_AccountError(String suggestedNames) {
        this.byteArray.writeByte(11);
        this.byteArray.writeString(suggestedNames);
        this.byteArray.writeString("");
    }

    public C_AccountError(int errorCode, String emailAddressCookie) {
        this.byteArray.writeByte(errorCode);
        this.byteArray.writeString("");
        this.byteArray.writeString(emailAddressCookie);
    }

    @Override
    public int getC() {
        return 26;
    }

    @Override
    public int getCC() {
        return 12;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}