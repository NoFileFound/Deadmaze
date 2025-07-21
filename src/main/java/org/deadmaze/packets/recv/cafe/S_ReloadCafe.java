package org.deadmaze.packets.recv.cafe;

// Imports
import java.util.concurrent.TimeUnit;
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.enums.SceneLoading;
import org.deadmaze.packets.RecvPacket;

@SuppressWarnings("unused")
public final class S_ReloadCafe implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        if(client.sceneLoadingInfo != SceneLoading.LOADED) {
            return;
        }

        if(client.reloadCafeTimer.getRemainingTime() <= 0) {
            client.getParseCafeInstance().reloadCafeTopics();
            client.reloadCafeTimer.schedule(() -> {}, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public int getC() {
        return 30;
    }

    @Override
    public int getCC() {
        return 40;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}