package org.deadmaze.command.commands.fashionsquad;

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
        name = "lsfs",
        description = "Lists connected fashion squad team and their channels.",
        permission = {Command.CommandPermission.FASHIONSQUAD, Command.CommandPermission.MODERATOR, Command.CommandPermission.ADMINISTRATOR}
)
@SuppressWarnings("unused")
public final class Lsfs implements CommandHandler {
    @Override
    public void execute(Client player, Server server, List<String> args) {
        List<String> staffInfo = new ArrayList<>();
        for(Client client : server.getPlayers().values()) {
            if(!client.isGuest() && client.getAccount().getStaffRoles().contains("FashionSquad")) {
                staffInfo.add(client.playerCommunity + "_" + client.getPlayerName() + "_" + client.getRoomName());
            }
        }
        player.sendPacket(new C_OnlineStaffTeam(10, staffInfo));
    }
}