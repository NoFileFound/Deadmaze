package org.deadmaze.packets.recv.informations;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.libraries.SrcRandom;
import org.deadmaze.packets.RecvPacket;

// Packets
import org.deadmaze.packets.send._105.C_SetInitialHazardousDamageIndex;
import org.deadmaze.packets.send.informations.C_SetAllowEmailAddress;
import org.deadmaze.packets.send.language.C_ClientVerification;
import org.deadmaze.packets.send.login.C_Handshake;

@SuppressWarnings("unused")
public final class S_Handshake implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        short version = data.readShort();
        String language = data.readString();
        String connectionToken = data.readString();
        String playerType = data.readString();

        data.readString(); // browser info
        data.readInt(); // loader stage size.
        data.readString(); // ccfData
        data.readString(); // font name hash
        data.readString(); // server string
        data.readInt(); // referrer
        data.readInt(); // time since you opened the game.
        data.readString(); // game name, empty in Dead maze

        Application.getLogger().debug(Application.getTranslationManager().get("newconnection", client.getIpAddress(), playerType));
        if(version != Application.getSwfInfo().version || !connectionToken.equals(Application.getSwfInfo().connection_key)) {
            System.out.println(version);
            System.out.println(connectionToken);
            Application.getLogger().warn(Application.getTranslationManager().get("fakeswfconnection", client.getIpAddress()));
            client.closeConnection();
            return;
        }

        client.playerType = playerType;
        client.sendPacket(new C_Handshake(client.getServer().getPlayersCount(), language, client.getCountryLangue()));
        client.sendPacket(new C_SetInitialHazardousDamageIndex(Application.getPropertiesInfo().hazardousdamageindex));
        client.sendPacket(new C_SetAllowEmailAddress());
        client.verCode = SrcRandom.RandomNumber(1000000, 999999999);
        client.sendPacket(new C_ClientVerification(client.verCode));
    }

    @Override
    public int getC() {
        return 28;
    }

    @Override
    public int getCC() {
        return 1;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}