package org.deadmaze.packets.recv._105;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.libraries.Pair;
import org.deadmaze.packets.RecvPacket;
import org.deadmaze.packets.SendPacket;

@SuppressWarnings("unused")
public class S_PlayerMovement implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        int unk1 = data.readByte();
        int posX = data.readShort();
        int posY = data.readShort();

        System.out.println(unk1 + " " + posX + " " + posY);

        client.playerPosition = new Pair<>(posX, posY);



        /// TODO: Implement
    }

    @Override
    public int getC() {
        return 105;
    }

    @Override
    public int getCC() {
        return 2;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}