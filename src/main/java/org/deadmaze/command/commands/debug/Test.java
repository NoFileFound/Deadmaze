package org.deadmaze.command.commands.debug;

// Imports
import java.util.List;

import org.bytearray.ByteArray;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;
import org.deadmaze.packets.SendPacket;


@Command(
        name = "test",
        description = "",
        permission = {Command.CommandPermission.DEBUG_ONLY}
)
@SuppressWarnings("unused")
public final class Test implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        player.sendPacket(new SendPacket() {
            @Override
            public int getC() {
                return 24;
            }

            @Override
            public int getCC() {
                return 1;
            }

            @Override
            public byte[] getPacket() {
                return new ByteArray().writeShort((short) 10).writeShort((short)20).toByteArray();
            }
        });
    }
}