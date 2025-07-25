package org.deadmaze.packets.send._112;

// Imports
import java.util.List;
import org.bytearray.ByteArray;
import org.deadmaze.packets.SendPacket;

public final class C_PlayerAddPoses implements SendPacket {
    private final ByteArray byteArray = new ByteArray();

    public C_PlayerAddPoses(List<Integer> poses) {
        this.byteArray.writeUnsignedShort(poses.size());
        for(int pose : poses) {
            this.byteArray.writeUnsignedShort(pose);
        }
    }

    @Override
    public int getC() {
        return 112;
    }

    @Override
    public int getCC() {
        return 36;
    }

    @Override
    public byte[] getPacket() {
        return this.byteArray.toByteArray();
    }
}