package org.deadmaze.packets.recv.modopwet;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.enums.SceneLoading;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_ModopwetChatNotification implements RecvPacket {
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
            client.isSubscribedModoNotifications = data.readBoolean();

            int sz = data.readByte();
            while(sz-- > 0) {
                client.getModopwetChatNotificationCommunities().add(data.readString());
            }
        }
    }

    @Override
    public int getC() {
        return 25;
    }

    @Override
    public int getCC() {
        return 12;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}