package org.deadmaze.packets.recv.cafe;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.enums.SceneLoading;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_DeleteAllCafePlayerPosts implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        if(client.sceneLoadingInfo != SceneLoading.LOADED || client.isGuest()) {
            Application.getLogger().debug(Application.getTranslationManager().get("abnormalactivity", client.getPlayerName(), "S_DeleteAllCafePlayerPosts"));
            client.getServer().getTempBlackList().add(client.getIpAddress());
            client.closeConnection();
            return;
        }

        client.getParseCafeInstance().sendDeleteAllCafePlayerPosts(data.readInt(), data.readString());
    }

    @Override
    public int getC() {
        return 30;
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