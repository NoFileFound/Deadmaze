package org.deadmaze.command.commands.funcorp;

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
        name = "lsfc",
        description = "Lists connected fun corps and their channels.",
        permission = {Command.CommandPermission.FUNCORP, Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR}
)
@SuppressWarnings("unused")
public final class Lsfc implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        List<String> staffInfo = new ArrayList<>();
        for(Client client : server.getPlayers().values()) {
            if(!client.isGuest() && client.getAccount().getStaffRoles().contains("FunCorp")) {
                staffInfo.add(client.playerCommunity + "_" + client.getPlayerName() + "_" + client.getRoomName());
            }
        }
        player.sendPacket(new C_OnlineStaffTeam(9, staffInfo));
    }
}