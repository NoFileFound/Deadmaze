package org.deadmaze.packets.send._105;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.packets.SendPacket;

public final class C_AddPlayerToWorld implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    /// TODO: INVESTIGATE
    public C_AddPlayerToWorld(Client player) {
        String[] playerLook = player.getAccount().getPlayerLook().split(";")[0].split(",");
        String[] playerClothes = player.getAccount().getPlayerLook().split(";")[1].split(",");

        this.byteArray.writeString(player.getPlayerName());
        this.byteArray.writeInt(player.getSessionId());
        this.byteArray.writeShort(player.playerPosition.getFirst().shortValue());
        this.byteArray.writeShort(player.playerPosition.getSecond().shortValue());
        this.byteArray.writeUnsignedByte(255); /// ???
        this.byteArray.writeBoolean(true); /// ???
        this.byteArray.writeBoolean(false); /// ???
        this.byteArray.writeBoolean(false); /// ???
        this.byteArray.writeByte(20); /// move speed
        this.byteArray.writeUnsignedByte(1);
        this.byteArray.writeUnsignedByte(0); /// ???
        this.byteArray.writeUnsignedByte(100); /// ???
        this.byteArray.writeByte(player.getAccount().getPlayerGender());
        this.byteArray.writeInt(Integer.parseInt(playerLook[0])); /// SKIN COLOR
        this.byteArray.writeInt(Integer.parseInt(playerLook[1])); /// HAIR COLOR
        this.byteArray.writeInt(Integer.parseInt(playerLook[2])); /// UNDERWEAR COLOR
        this.byteArray.writeString(""); /// Chien
        this.byteArray.writeUnsignedByte(playerClothes.length);
        for (String playerClothe : playerClothes) {
            this.byteArray.writeUnsignedShort(Integer.parseInt(playerClothe));
        }

        this.byteArray.writeUnsignedByte(0); /// ???, $Dechirure_
        this.byteArray.writeUnsignedByte(0); /// ???
        this.byteArray.writeUnsignedByte(0); /// ???


        this.byteArray.writeUnsignedShort(120); /// WEAPON ID.
        this.byteArray.writeUnsignedShort(0); /// WEAPON ID. (For quests), 0 for switching to your weapon.
        this.byteArray.writeUnsignedByte(1); /// ???
        this.byteArray.writeUnsignedByte(1); /// ???
        this.byteArray.writeBoolean(false); /// ???
        this.byteArray.writeByte(4); /// ???
        this.byteArray.writeBoolean(false); /// ???


        this.byteArray.writeInt(15896980); /// ???
        this.byteArray.writeShort((short) player.getAccount().getTitleNumber());
        this.byteArray.writeBoolean(false); /// ???
        this.byteArray.writeUnsignedByte(0); /// TOTEM ID
    }

    @Override
    public int getC() {
        return 105;
    }

    @Override
    public int getCC() {
        return 51;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}