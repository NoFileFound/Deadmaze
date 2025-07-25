package org.deadmaze.packets.recv._105;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.packets.RecvPacket;
import org.deadmaze.packets.SendPacket;

@SuppressWarnings("unused")
public class S_UpdateProfileCreationAppearance implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        if(!client.getAccount().getPlayerLook().isEmpty()) {
            client.closeConnection();
            return;
        }

        int genderType = data.readByte();
        int hairColor = data.readInt();
        int skinColor = data.readInt();
        int underwearColor = data.readInt();
        int headId = data.readShort();
        int topId = data.readShort();
        int pantsId = data.readShort();
        int shoesId = data.readShort();

        /// TODO: Implement
        /// TODO: Look: 16112320,2631720,15461355;0,50000,5000,40001,10005,30010

        client.sendPacket(new SendPacket() {
            @Override
            public int getC() {
                return 105;
            }

            @Override
            public int getCC() {
                return 9;
            }

            @Override
            public byte[] getPacket() {
                return new ByteArray()
                        .writeInt(client.getSessionId())
                        .writeUnsignedByte(1)
                        .writeUnsignedByte(0)
                        .writeUnsignedByte(100)
                        .writeByte(genderType)
                        .writeInt(skinColor)
                        .writeInt(hairColor)
                        .writeInt(underwearColor)
                        .writeString("")
                        .writeUnsignedByte(6)

                        .writeUnsignedShort(0)
                        .writeUnsignedShort(50000)
                        .writeUnsignedShort(5000)
                        .writeUnsignedShort(40001)
                        .writeUnsignedShort(10005)
                        .writeUnsignedShort(30010)


                        .writeUnsignedByte(0)
                        .writeUnsignedByte(0)
                        .writeUnsignedByte(0)
                        .toByteArray();
            }
        });
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