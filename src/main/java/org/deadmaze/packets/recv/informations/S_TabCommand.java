package org.deadmaze.packets.recv.informations;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.enums.SceneLoading;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_TabCommand implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        if(client.sceneLoadingInfo != SceneLoading.LOADED || client.isGuest()) {
            Application.getLogger().debug(Application.getTranslationManager().get("abnormalactivity", client.getPlayerName(), "S_TabCommand"));
            client.getServer().getTempBlackList().add(client.getIpAddress());
            client.closeConnection();
            return;
        }

        client.getServer().getCommandHandler().invokeCommand(client, data.readString(), true);
    }

    @Override
    public int getC() {
        return 28;
    }

    @Override
    public int getCC() {
        return 48;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}