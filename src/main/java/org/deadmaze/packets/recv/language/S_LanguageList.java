package org.deadmaze.packets.recv.language;

// Imports
import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.packets.RecvPacket;

// Packets
import org.deadmaze.packets.send.language.C_LanguageList;

@SuppressWarnings("unused")
public final class S_LanguageList implements RecvPacket {
    @Override
    public void handle(Client client, int fingerPrint, ByteArray data) {
        client.sendPacket(new C_LanguageList(client.getCountryLangue()));
    }

    @Override
    public int getC() {
        return 176;
    }

    @Override
    public int getCC() {
        return 2;
    }

    @Override
    public boolean isLegacyPacket() {
        return false;
    }
}