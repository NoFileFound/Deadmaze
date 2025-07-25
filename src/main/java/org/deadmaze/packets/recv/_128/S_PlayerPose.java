package org.deadmaze.packets.recv._128;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.enums.SceneLoading;
import org.deadmaze.packets.RecvPacket;

// Packets
import org.deadmaze.packets.send._112.C_PlayerPose;

@SuppressWarnings("unused")
public final class S_PlayerPose implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        if(client.sceneLoadingInfo != SceneLoading.LOADED) {
            Application.getLogger().debug(Application.getTranslationManager().get("abnormalactivity", client.getPlayerName(), "S_PlayerPose"));
            client.getServer().getTempBlackList().add(client.getIpAddress());
            client.closeConnection();
            return;
        }

        int poseId = data.readByte();
        if(!client.getAccount().getPlayerPoses().contains(poseId)) {
            return;
        }

        client.getRoom().sendAll(new C_PlayerPose(client.getSessionId(), poseId, false));
    }

    @Override
    public int getC() {
        return 128;
    }

    @Override
    public int getCC() {
        return 23;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}