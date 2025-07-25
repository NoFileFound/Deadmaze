package org.deadmaze.packets.recv._105;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Application;
import org.deadmaze.Client;
import org.deadmaze.enums.SceneLoading;
import org.deadmaze.libraries.Pair;
import org.deadmaze.packets.RecvPacket;

// Packets
import org.deadmaze.packets.send._105.C_AddPlayerToWorld;
import org.deadmaze.packets.send._105.C_InitLoadingWorld;
import org.deadmaze.packets.send._105.C_RenderWorldMap;
import org.deadmaze.utils.Utils;

@SuppressWarnings("unused")
public final class S_EnterLoadingScene implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        int sceneType = data.readByte();
        switch (sceneType) {
            case 88:
                client.screenResolution = new Pair<>((int)data.readShort(), (int)data.readShort());
                /// TODO: Implement the required packets after receiving the screen resolution.
                break;
            case 80:
                boolean isPlayerLoading = data.readBoolean();
                if(isPlayerLoading) {
                    switch (client.sceneLoadingInfo) {
                        case INIT -> {
                            client.sceneLoadingInfo = SceneLoading.LOADING;
                            /// TODO: Implement the required packets before loading the world.
                            client.sendPacket(new C_InitLoadingWorld(1, 38));
                        }
                        case LOADING -> {
                            /// TODO: Implement the required packets before rendering the map.
                            client.sendPacket(new C_RenderWorldMap(190, 285, Utils.getResourceFileContent("map.zlib")));
                            /// TODO: Implement the required packets after loading the world.

                            if(!client.getAccount().getPlayerLook().isEmpty()) {
                                client.sendRoomPlayers();
                            }
                        }
                    }
                } else {
                    client.sceneLoadingInfo = SceneLoading.LOADED;
                    /// TODO: Implement the required packets after the world is loaded.
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