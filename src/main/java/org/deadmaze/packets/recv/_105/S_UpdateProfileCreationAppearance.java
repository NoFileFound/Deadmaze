package org.deadmaze.packets.recv._105;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public class S_UpdateProfileCreationAppearance implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        int genderType = data.readByte();
        int hairColor = data.readInt();
        int skinColor = data.readInt();
        int underwearColor = data.readInt();
        int headId = data.readShort();
        int topId = data.readShort();
        int pantsId = data.readShort();
        int shoesId = data.readShort();

        /// TODO: Implement
    }

    @Override
    public int getC() {
        return 105;
    }

    @Override
    public int getCC() {
        return 99;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}