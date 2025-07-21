package org.deadmaze.packets.recv.cafe;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.enums.SceneLoading;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_VoteCafePost implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        if(client.sceneLoadingInfo != SceneLoading.LOADED || client.isGuest()) {
            Application.getLogger().debug(Application.getTranslationManager().get("abnormalactivity", client.getPlayerName(), "S_VoteCafePost"));
            client.getServer().getTempBlackList().add(client.getIpAddress());
            client.closeConnection();
            return;
        }

        client.getParseCafeInstance().voteCafePost(data.readInt(), data.readInt(), data.readBoolean());
    }

    @Override
    public int getC() {
        return 30;
    }

    @Override
    public int getCC() {
        return 46;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}