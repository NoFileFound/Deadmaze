package org.deadmaze.packets.recv.modopwet;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.enums.SceneLoading;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_WatchReport implements RecvPacket {
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

        if(client.hasStaffPermission("Modo", "Modopwet") || client.hasStaffPermission("TrialModo", "Modopwet")) {
            client.getParseModopwetInstance().sendWatchReport(data.readString(), data.readBoolean());
        }
    }

    @Override
    public int getC() {
        return 25;
    }

    @Override
    public int getCC() {
        return 24;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}