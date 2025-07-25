package org.deadmaze.command.commands.modo;

// Imports
import java.util.ArrayList;
import java.util.List;
import org.deadmaze.Client;
import org.deadmaze.Server;
import org.deadmaze.command.Command;
import org.deadmaze.command.CommandHandler;

// Packets
import org.deadmaze.packets.send.level.C_OnlineStaffTeam;

@Command(
        name = "lsmodo",
        description = "Lists connected moderators and administrators and their channels.",
        permission = {Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR}
)
@SuppressWarnings("unused")
public final class Lsmodo implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        List<String> staffInfo = new ArrayList<>();
        for(Client client : server.getPlayers().values()) {
            if(!client.isGuest() && client.hasStaffPermission("Modo", "")) {
                staffInfo.add(client.playerCommunity + "_" + client.getPlayerName() + "_" + client.getRoomName());
            }
        }
        player.sendPacket(new C_OnlineStaffTeam(3, staffInfo));
    }
}