package org.deadmaze.packets.send._105;

// Imports
import org.deadmaze.packets.SendPacket;

public final class C_InitializeProfileCreation implements SendPacket {
    @Override
    public int getC() {
        return 105;
    }

    @Override
    public int getCC() {
        return 99;
    }

    @Override
    public byte[] getPacket() {
        return new byte[0];
    }
}