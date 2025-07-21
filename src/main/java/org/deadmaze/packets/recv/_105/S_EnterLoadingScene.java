package org.deadmaze.packets.recv._105;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.enums.SceneLoading;
import org.deadmaze.libraries.Pair;
import org.deadmaze.packets.RecvPacket;

// Packets
import org.deadmaze.packets.send._105.C_InitLoadingWorld;
import org.deadmaze.packets.send._105.C_RenderWorldMap;

@SuppressWarnings("unused")
public class S_EnterLoadingScene implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        int sceneType = data.readByte();
        switch (sceneType) {
            case 88:
                client.screenResolution = new Pair<>((int)data.readShort(), (int)data.readShort()); /// 800x600
                break;
            case 80:
                boolean isLoading = data.readBoolean();
                if(!isLoading) {
                    client.sceneLoadingInfo = SceneLoading.LOADED;
                } else {
                    if(client.sceneLoadingInfo == SceneLoading.INIT) {
                        client.sceneLoadingInfo = SceneLoading.LOADING;
                        client.sendPacket(new C_InitLoadingWorld(1, 38));
                    } else {
                        client.sendPacket(new C_RenderWorldMap(190, 285, new byte[0]));
                    }
                }
                break;
            case  70:
                /// TODO: Investigate / RESEARCH.
                break;
            default:
                Application.getLogger().warn(Application.getTranslationManager().get("invalidscenetype", sceneType));
        }
    }

    @Override
    public int getC() {
        return 105;
    }

    @Override
    public int getCC() {
        return 14;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}