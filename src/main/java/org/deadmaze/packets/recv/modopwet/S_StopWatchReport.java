package org.deadmaze.packets.recv.modopwet;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.enums.SceneLoading;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_StopWatchReport implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        if(client.isGuest()) {
            client.closeConnection();
            client.getServer().getTempBlackList().add(client.getIpAddress());
            return;
        }

        if(client.sceneLoadingInfo != SceneLoading.LOADED) {
            return;
        }

        if(client.lastWatchedClient != null) {
            client.lastWatchedClient.getCurrentWatchers().clear();
            client.lastWatchedClient = null;
            client.getParseModopwetInstance().sendWatchPlayer("");
            client.isHidden = false;
            client.sendEnterRoom(client.getServer().getRecommendedRoom(""));
        }
    }

    @Override
    public int getC() {
        return 25;
    }

    @Override
    public int getCC() {
        return 28;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}