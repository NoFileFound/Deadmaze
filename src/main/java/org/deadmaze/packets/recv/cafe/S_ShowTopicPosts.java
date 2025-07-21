package org.deadmaze.packets.recv.cafe;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.enums.SceneLoading;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_ShowTopicPosts implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        if(client.sceneLoadingInfo != SceneLoading.LOADED) {
            Application.getLogger().debug(Application.getTranslationManager().get("abnormalactivity", client.getPlayerName(), "S_ShowTopicPosts"));
            client.getServer().getTempBlackList().add(client.getIpAddress());
            client.closeConnection();
            return;
        }

        client.getParseCafeInstance().sendTopicPosts(data.readInt(), null);
    }

    @Override
    public int getC() {
        return 30;
    }

    @Override
    public int getCC() {
        return 41;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}