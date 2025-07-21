package org.deadmaze.packets.recv.login;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.enums.SceneLoading;
import org.deadmaze.packets.RecvPacket;

// Packets
import org.deadmaze.packets.send.login.C_SurveyMakeVote;

@SuppressWarnings("unused")
public final class S_SurveyMakeVote implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        if(client.sceneLoadingInfo != SceneLoading.LOADED || client.isGuest()) {
            Application.getLogger().debug(Application.getTranslationManager().get("abnormalactivity", client.getPlayerName(), "S_SurveyMakeVote"));
            client.getServer().getTempBlackList().add(client.getIpAddress());
            client.closeConnection();
            return;
        }

        int playerId = data.readInt();
        for (Client player : client.getServer().getPlayers().values()) {
            if (!player.isGuest() && player.getAccount().getId() == playerId && player.sceneLoadingInfo == SceneLoading.LOADED) {
                player.sendPacket(new C_SurveyMakeVote(data.readByte()));
            }
        }
    }

    @Override
    public int getC() {
        return 26;
    }

    @Override
    public int getCC() {
        return 17;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}