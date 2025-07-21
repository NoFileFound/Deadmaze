package org.deadmaze.packets.recv.cafe;

// Imports
import java.util.concurrent.TimeUnit;
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.enums.SceneLoading;
import org.deadmaze.packets.RecvPacket;

// Packets
import org.deadmaze.packets.send.informations.C_TranslationMessage;

@SuppressWarnings("unused")
public final class S_CreateNewTopic implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        if(client.sceneLoadingInfo != SceneLoading.LOADED) {
            Application.getLogger().debug(Application.getTranslationManager().get("abnormalactivity", client.getPlayerName(), "S_CreateNewTopic"));
            client.getServer().getTempBlackList().add(client.getIpAddress());
            client.closeConnection();
            return;
        }

        if(client.getServer().createCafeTopicTimer.get(client.getPlayerName()).getRemainingTime() <= 0) {
            client.getParseCafeInstance().sendNewTopic(data.readString(), data.readString());
            client.getServer().createCafeTopicTimer.get(client.getPlayerName()).schedule(() -> {}, TimeUnit.MINUTES);
        } else {
            client.sendPacket(new C_TranslationMessage("", "$AttendreNouveauSujet", new String[]{String.valueOf(client.getServer().createCafeTopicTimer.get(client.getPlayerName()).getRemainingTime() / 60000)}));
        }
    }

    @Override
    public int getC() {
        return 30;
    }

    @Override
    public int getCC() {
        return 44;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}